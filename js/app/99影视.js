import "assets://js/lib/crypto-js.js";


//let host = 'http://111.170.140.70:19987/app/bn';

let host, player, parser_api, uuid, appkey, versionName, name, buildSignature;

async function init(cfg) {
uuid = getUUID();
host = cfg.ext.host;
appkey = cfg.ext.appkey;
versionName = cfg.ext.versionName;
name = cfg.ext.name;
buildSignature = cfg.ext.buildSignature;
}


function getUUID() {
    const pattern = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx';
    return pattern.replace(/[xy]/g, function(c) {
        const r = Math.random() * 16 | 0;
        return (c === 'x') ? r.toString(16) : 
               (r & 0x3 | 0x8).toString(16);
    });
}

function randomHex(len) {
    const chars = '0123456789abcdef';
    let result = '';
    for (let i = 0; i < len; i++) {
        result += chars[Math.floor(Math.random() * 16)];
    }
    return result;
}

function en(data) {
    const key = CryptoJS.enc.Utf8.parse(uuid.replace(/-/g,'')); //utf8 key 
    const iv = randomHex(32);
    const encrypted = CryptoJS.AES.encrypt(data, key, {
        iv: CryptoJS.enc.Hex.parse(iv), //hex iv
        mode: CryptoJS.mode.CBC,
        padding: CryptoJS.pad.Pkcs7
    });
    const hexbody = iv + encrypted.ciphertext.toString(CryptoJS.enc.Hex)
    return CryptoJS.enc.Hex.parse(hexbody).toString(CryptoJS.enc.Base64)
}

function de(data) {
    const hex = CryptoJS.enc.Base64.parse(data).toString(CryptoJS.enc.Hex)
    const key = CryptoJS.enc.Utf8.parse(uuid.replace(/-/g,'')); //utf8 key 
    const iv = hex.slice(0, 32);
    const cipherHex = hex.slice(32)
    const decrypted = CryptoJS.AES.decrypt({ ciphertext: CryptoJS.enc.Hex.parse(cipherHex) }, key, {
        iv: CryptoJS.enc.Hex.parse(iv),
        mode: CryptoJS.mode.CBC,
        padding: CryptoJS.pad.Pkcs7
    });
    return decrypted.toString(CryptoJS.enc.Hex);
}

async function request(body, url) {
body.timestamp = Date.now()
body.nonce = CryptoJS.enc.Hex.parse(randomHex(32)).toString(CryptoJS.enc.Base64)
let enbody = en(JSON.stringify(body))
let hh = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.6299.95 Safari/537.36",
    "sign": CryptoJS.SHA256(`${enbody}:${body.timestamp}:${body.nonce}:${body.token||''}:${appkey}`).toString(),
    "appkey": appkey,
    "client_type": "android",
    "api_version": "v1",
    "uuid": uuid,
    "nonce": body.nonce,
    "version": "",
    "timestamp": body.timestamp,
    "Content-Type": "application/json; charset=utf-8"
}
let dd = (await req(url, {
    body: enbody,
    headers: hh,
    method: 'POST'
})).content

return JSON.parse(unzipX(de(dd)))
}

//分类
async function home (filter) {
let data = {
"v": versionName,
"n": name,
"s": buildSignature,
"pl": "1",
"apiVersion": "v2",
"token": "",
"timestamp": "",
"nonce": ""
}
let res = await request(data, `${host}/app/systemInit`)
player = Object.values(res.player);
parser_api = res.parser_api

//一级
let classes = res.categorys.data.map(tp => ({
  type_id: tp.id,
  type_name: tp.name.replace(/ /,'')
})).filter(item => item.type_name !== "公告" && item.type_name !== "动漫资讯");
//二级
let filterObj = {};
for (let type of res.categorys.data) {
  let dd = JSON.parse(type.type_extend);
  let filters = [];

  if (dd.class.trim()) {
    filters.push({
      key: 'class',
      name: '类型',
      value: dd.class.split(',').map(v => ({ n: v, v }))
    });
  }

  if (dd.area.trim()) {
    filters.push({
      key: 'area',
      name: '地区',
      value: dd.area.split(',').map(v => ({ n: v, v }))
    });
  }

  if (dd.year.trim()) {
    filters.push({
      key: 'year',
      name: '年份',
      value: dd.year.split(',').map(v => ({ n: v, v }))
    });
  }

  filters.push({
    key: 'sort',
    name: '排序',
    value: [{n:"最热",v:"vod_hits"},{n:"最新",v:"vod_time"},{n:"高分",v:"vod_score"}]
  });

  filterObj[type.id] = filters;
}

return JSON.stringify({
    class: classes,
    filters: filterObj,
//    list: videos
});
}


//主页推荐
async function homeVod() {
}

//分类
async function category (tid, pg, filter, extend) {
let data = {
	"kw": "",
	"page": pg,
	"limit": 21,
	"pid": tid,
	"orderBy": extend.sort || 'vod_hits',
	"isCategory": 1,
	"token": "",
	"timestamp": "",
	"nonce": ""
}
if (extend.class) data.class = extend.class
if (extend.area) data.area = extend.area
if (extend.year) data.year = extend.year

let res = await request(data, `${host}/vod/search`)
let videos = res.data.map(item => ({
    vod_id: item.id,
    vod_name: item.name,
    vod_pic: `${item.pic}@Referer=${item.pic}@User-Agent=Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36`,
    vod_remarks: item.remarks,
    vod_year: item.year
}));
  return JSON.stringify({
    page: pg,
    pagecount: 99999,
    limit: videos.length,
    total: 99999,
    list: videos
});
}

//详情
async function detail (id) {
let data = {
	"id": id,
	"eps": "",
	"v": versionName,
	"pl": 1,
	"token": "",
	"timestamp": "",
	"nonce": ""
}
let res = (await request(data, `${host}/vod/detail`)).data

let pMap = Object.fromEntries(player.map(i => [i.code, i]))
let fromArr = res.play_from.split('$$$')
let urlArr = res.play_url.split('$$$')

const exclude = ['bfzym3u8', 'tym3u8', 'zjm3u8', 'lzm3u8', 'sdm3u8', 'kbm3u8', 'bjm3u8', 'xkm3u8', 'tpm3u8', 'hnm3u8', 'wjm3u8', 'ffm3u8', '99m3u8', 'dbm3u8', 'rym3u8', 'mzm3u8', 'mym3u8', 'wwm3u8', 'mtm3u8', 'snm3u8', 'okm3u8', 'wolong', 'http', 'ruyi', 'rym3u8', 'yym3u8', 'ikm3u8', 'jsm3u8', 'co_egg', 'NSYS'];

// 过滤掉 exclude 中的线路
let filtered = fromArr.map((code, idx) => ({code, url: urlArr[idx]}))
                     .filter(item => !exclude.includes(item.code))
                     .sort((a, b) => (pMap[b.code]?.sort || 0) - (pMap[a.code]?.sort || 0));

let play_from = filtered.map(i => `${pMap[i.code]?.name?.trim() || i.code} [${i.code}]` || i.code).join('$$$')
let play_url = filtered.map(item => 
  item.url.split('#').map(ep => {
    const [name, url] = ep.split('$')
    const p = pMap[item.code]
    return `${name}$${url}@@${p?.parseUrl || ''}`
  }).join('#')
).join('$$$')

var vod = {
    "type_name": res.class,
    "vod_year": res.year,
    "vod_area": res.area,
    "vod_remarks": res.remarks,
    "vod_actor": res.actor,
    "vod_director": res.director,
    "vod_content": res.content,
    "vod_play_from": play_from, 
    "vod_play_url": play_url
    }

return JSON.stringify({ list: [vod] })
}


//播放
async function play (flag, id, flags) {
let [url, parse_id] = id.split('@@')
if (/\.m3u8|\.mp4/i.test(url)) return JSON.stringify({parse: 0,url: url})
if (!parse_id.trim()) return JSON.stringify({parse: 0,url: url})

const idArr = parse_id?.split(',')?.map(i => i.trim()).filter(Boolean)
const apiList = idArr.map(pid => 
  parser_api.find(i => i.id == pid)?.api_url
).filter(Boolean)

let play_url = ''
for (const apiUrl of apiList) {
  try {
    let res = JSON.parse((await req(apiUrl + url)).content)
    if (res.url) {
      play_url = res.url
      break
    }
  } catch (err) {}
}

return JSON.stringify({parse: 0,url: play_url})
}

//搜索
async function search (wd, quick, pg=1) {
let data = {
"kw":wd,
"page":pg,
"limit":21,
"orderBy":"vod_hits_month",
"sort":"desc",
"token":"",
"timestamp":"",
"nonce":""
}

let res = await request(data, `${host}/vod/search`)
let videos = res.data.map(item => ({
    vod_id: item.id,
    vod_name: item.name,
    vod_pic: item.pic.includes('url=') ? `${item.pic.split('url=')[1]}@Referer=${item.pic.split('url=')[1]}@User-Agent=Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36` : item.pic,
    vod_remarks: item.remarks,
    vod_year: item.year
}));

return JSON.stringify({ limit: videos.length, list: videos});
}

export function __jsEvalReturn() {
  return {
      init: init,
      home: home,
      homeVod: homeVod,
      category: category,
      detail: detail,
      play: play,
      search: search
  };
}