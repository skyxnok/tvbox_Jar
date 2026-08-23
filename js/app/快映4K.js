//www.ky4k.top
let host, playinfo;

async function init(cfg) {
    try {
        const response = await req('https://www.pjb777.top/ky4kbgq7b273.json', {headers: {"User-Agent": "Dalvik/2.1.0 (Linux; U; Android 9; 23116PN5BC Build/PQ3B.190801.04011825)"}});
        const config = JSON.parse(response.content);
        host = config.apiDomain || 'https://www.kanzurm65ak.top';
        playinfo = JSON.parse(de((await req(`${host}/api.php/appfoxs/config`,{headers: gethh()} )).content)).data;
    }catch{
    }
}


//请求头
function hh() {
return {
//    "cache-control": "no-cache",
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"
}
}

//签名请求头
function gethh(data) {
let t = new Date().getTime().toString()
let suiji = Math.floor(100000 + Math.random() * 900000);
let sign = md5X(`47aa22547fcada31dd7bd35cab492326kuaiying4k${t}${suiji}${data||''}`).toLowerCase()
return {
    "x-security-auth": `${t}|${suiji}|${sign}`,
    "user-agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36",
    "content-type": "application/json; charset=utf-8"
}
}

//解密
function de(data) {
const key = '3dd7d42dc2496f1d';
const iv  = 'd1f6942cd24d7dd3';
//模式  加密  内容  内容是不是b64  key  iv   输出b64
return aesX('AES/CBC/PKCS5', false, data, true, key, iv, false);
}

//分类
async function home (filter) {
let ccc = JSON.parse((await req(`${host}/api.php/appfox/init`,{headers: hh() })).content)

//一级
let classes = ccc.data.type_list.map(tp => ({
  type_id: tp.type_id,
  type_name: tp.type_name
})).filter(item => item.type_name !== "学日语" && item.type_name !== "全部");

//二级
let filterObj = ccc.data.type_list.reduce((acc, tp) => {
  if (tp.filter_type_list) {
    acc[tp.type_id] = tp.filter_type_list.map(filter => ({
      key: filter.name,
      name: filter.list[0],
      value: filter.list.map(item => ({
        n: item,
        v: item
      }))
    }));
  }
  return acc;
}, {});


//推荐
let home = JSON.parse((await req(`${host}/api.php/appfox/nav_video?id=2`,{headers: hh() })).content).data

let videos = home[0]?.categories?.flatMap(c =>  c.videos.map(item => ({
  vod_id: item.vod_id,
  vod_name: item.vod_name,
  vod_pic: item.vod_pic,
  vod_remarks: item.vod_remarks,
  vod_year: item.vod_pubdate.split('-')[0] || ''
})))

return JSON.stringify({ class: classes, filters: filterObj, list: videos });
}


//主页推荐
async function homeVod() {
}

//分类
async function category (tid, pg, filter, extend) {

let html = JSON.parse((await req(`${host}/api.php/appfox/vodList?type_id=${tid}&class=${extend.class || '全部'}&area=${extend.area || '全部'}&lang=${extend.lang || '全部'}&year=${extend.year || '全部'}&sort=${extend.sort || '最热'}&page=${pg}`, {
        headers: {"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"}
    })).content)

let videos = html.data.recommend_list.map(item => ({
    vod_id: item.vod_id,
    vod_name: item.vod_name,
    vod_pic: item.vod_pic,
    vod_remarks: item.vod_remarks,
    vod_year: ""
}));

return JSON.stringify({ page: pg, pagecount: 99999, limit: videos.length, total: 99999, list: videos });
}

//详情
async function detail (id) {
let html;
if (id.indexOf("vod_play_from") > -1){
html = JSON.parse(id)
}else{
html = JSON.parse(de((await req(`${host}/api.php/appfoxs/vod`, {
        body: JSON.stringify({ ac: "detail", ids: id }),
        headers: gethh(`{"ac":"detail","ids":"${id}"}`),
        method: 'POST'
    })).content)).list[0]
}

//去掉采集
const exclude = ['bfzym3u8', 'tym3u8', 'zjm3u8', 'lzm3u8', 'sdm3u8', 'kbm3u8', 'bjm3u8', 'xkm3u8', 'tpm3u8', 'hnm3u8', 'wjm3u8', 'ffm3u8', '99m3u8', 'dbm3u8', 'rym3u8', 'mzm3u8', 'mym3u8', 'wwm3u8', 'mtm3u8', 'snm3u8', 'okm3u8', 'wolong', 'http', 'ruyi', 'rym3u8', 'yym3u8', 'ikm3u8', 'jsm3u8', 'wjwsym3u8'];

// 高清优先关键词
const hdKeys = ['4K','4k','2K','2k','臻彩','真彩'];

// code映射名称
const playerMap = {};
playinfo.playerList.forEach(v=>playerMap[v.playerCode]=v.playerName);

const lines = html.vod_play_from.split('$$$');
const urls = html.vod_play_url.split('$$$');

let list = [];
for(let i=0;i<lines.length;i++){
  const code = lines[i];
  if(exclude.includes(code)) continue;
  const name = playerMap[code]||code;
  const url = urls[i].split('#').map(s=>s.replace('$',`$${code}@@`)).join('#');
  list.push({ name, url });
}

// 高清线路排前面
list.sort((a, b) => {
  const scoreA = hdKeys.some(tag => a.name.includes(tag)) ? 1 : 0;
  const scoreB = hdKeys.some(tag => b.name.includes(tag)) ? 1 : 0;
  return scoreB - scoreA;
});

const play_from = list.map(v=>v.name).join("$$$");
const play_url = list.map(v=>v.url).join("$$$");

const vod = {
  vod_class: html.vod_class,
  vod_year: html.vod_year,
  vod_area: html.vod_area,
  vod_remarks: html.vod_remarks,
  vod_actor: "",
  vod_director: "",
  vod_content: html.vod_content,
  vod_play_from: play_from,
  vod_play_url: play_url 
}

return JSON.stringify({ list: [vod] })
}


//播放
async function play (flag, id, flags) {
let ids = id.split('@@')
let plays = playinfo.jiexiDataList.find(p => {
  const codes = p.playerCode.split(',').map(code => code.trim());
  return codes.includes(ids[0]);
})?.url;


if (plays) {
let url = JSON.parse((await req(`${plays}${ids[1]}`, {
        headers: {"User-Agent": "Mozilla/5.0 (Linux; Android 4.2.1; M040 Build/JOP40D) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.59 Mobile Safari/537.36"}
    })).content).url
return JSON.stringify({parse: 0,url: url})
}
return JSON.stringify({parse: 0,url: ids[1]})
}

//搜索
async function search (wd, quick, pg=1) {

let html = JSON.parse(de((await req(`${host}/api.php/appfoxs/vod`, {
        body: JSON.stringify({ ac: "detail", wd: wd, pg: pg }),
        headers: gethh(`{"ac":"detail","wd":"${wd}","pg":"${pg||1}"}`),
        method: 'POST'
    })).content))

let videos = html.list.map(item => ({
//    vod_id: item.vod_id,
    vod_id: JSON.stringify({'vod_play_from':item.vod_play_from,'vod_play_url':item.vod_play_url}),
    vod_name: item.vod_name,
    vod_pic: item.vod_pic,
    vod_remarks: item.vod_remarks,
    vod_year: item.vod_year
}));

return JSON.stringify({ limit: videos.length, list: videos });
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