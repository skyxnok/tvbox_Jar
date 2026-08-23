//官网:mk1080.top
//let host = 'https://mk1080.top';

let host;

async function init(cfg) {
try{
let res = (await req('https://mk1080.top/get.txt')).content.match(/.+/).join("")
host = res.includes('ok')  ? 'https://mk1080.top' : res
}catch(e){
host = 'https://mk1080.top'
}
}

//解密
function de(data) {
const key = 'c60d88b2eep53za8';
const iv  = 'c60d88b2eep53za8';
//模式  加密  内容  内容是不是b64  key  iv   输出b64
return aesX('AES/CBC/PKCS5', false, data, true, key, iv, false);
}

//加密
function en(data) {
const key = 'c60d88b2eep53za8';
const iv  = 'c60d88b2eep53za8';
//模式  加密  内容  内容是不是b64  key  iv   输出b64
return aesX('AES/CBC/PKCS5', true, data, false, key, iv, true)
}

//请求头
function hh() {
let t = Math.floor(Date.now() / 1000);
return {
    "app-version-code": "135",
    "app-ui-mode": "light",
    "app-user-device-id": "20e4e50fddcad37dfb5c7b10e344b29b3",
    "app-user-token": "9167bc7a247b7a8bb67942dabc903d6ba204b04623ae077252fb2ed860a72d6f",
    "app-api-verify-time": t,
    "app-api-verify-sign": en(t.toString()),
    "Content-Type": "application/x-www-form-urlencoded",
    "User-Agent": "okhttp/3.14.9"
}
}

//去掉采集+排序
function filterUrls(list) {
const exclude = ['bfzym3u8', 'tym3u8', 'zjm3u8', 'lzm3u8', 'sdm3u8', 'kbm3u8', 'bjm3u8', 'xkm3u8', 'tpm3u8', 'hnm3u8', 'wjm3u8', 'ffm3u8', '99m3u8', 'dbm3u8', 'mzm3u8', 'mym3u8', 'wwm3u8', 'mtm3u8', 'NMYS', 'YHDM', 'm3u8', 'zlyun', 'KYLG', 'LKDB', 'xnk', 'AK_4K'];

const highQualityTags = ['4k', '4K','2k', '2K', '臻彩'];

  // 第一步：过滤排除线路
  const filterList = list.filter(item => {
    const firstFrom = (item.urls[0] || {}).from;
    return !exclude.includes(firstFrom);
  });

  // 第二步：4K线路排前面
  filterList.sort((a, b) => {
    const showA = a.player_info.show || '';
    const showB = b.player_info.show || '';
    const scoreA = highQualityTags.some(tag => showA.includes(tag)) ? 1 : 0;
    const scoreB = highQualityTags.some(tag => showB.includes(tag)) ? 1 : 0;
    return scoreB - scoreA;
  });

  return filterList;
}

//请求
async function request(reqUrl, body) {
  const res = JSON.parse((await req(reqUrl, {
    body: body,
    headers: hh(),
    method: 'POST'
  })).content).data;
  return JSON.parse(de(res));
}

//分类
async function home (filter) {
let res = await request(`${host}/api.php/getappapi.index/initV119`, '');

//一级
let classes = res.type_list.map(tp => ({
  type_id: tp.type_id,
  type_name: tp.type_name
})).filter(item => item.type_name !== "全部");

//二级
let filterObj = res.type_list.reduce((acc, tp) => {
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
let videos = res.banner_list.map(item => ({
    vod_id: item.vod_id,
    vod_name: item.vod_name,
    vod_pic: item.vod_pic,
    vod_remarks: item.vod_remarks,
    vod_year: item.vod_year,
    style: {"type": "rect", "ratio": 1.485 }
}));

return JSON.stringify({ class: classes, filters: filterObj, list: videos });
}


//主页推荐
async function homeVod() {
}

//分类
async function category (tid, pg, filter, extend) {
let body = `area=${extend.area || ''}&year=${extend.year || ''}&type_id=${tid}&page=${pg}&sort=${extend.sort || '最热'}&lang=${extend.lang || ''}&class=${extend.class || ''}`
let res = (await request(`${host}/api.php/getappapi.index/typeFilterVodList`, body)).recommend_list;
let videos = res.map(item => ({
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
let res = await request(`${host}/api.php/getappapi.index/vodDetail`, `vod_id=${id}`);
//去掉采集
let playlist = filterUrls(res.vod_play_list)

let play_from = playlist.map(item => `${item.player_info.show} [${item.urls[0].from}]`).join('$$$')

let play_url = []; // 存放所有线路拼接完成后的字符串
let playlistLen = playlist.length; // 缓存线路总长度，减少重复读取
for (let i = 0; i < playlistLen; i++) { // 外层循环：遍历每条播放线路
  let playItem = playlist[i]; // 当前循环到的单条线路对象
  let p = playItem.player_info.parse; // 提取线路解析模板字符串
  let epList = playItem.urls; // 当前线路下全部剧集数组
  
  let urlArr = []; // 临时数组，存本条线路每一集拼接结果
  let epLen = epList.length; // 缓存当前线路剧集总数

  for (let j = 0; j < epLen; j++) { // 内层循环：遍历当前线路每一集
    let ep = epList[j]; // 取出单集信息对象
    let urls = p ? p.includes('http') ? ep.parse_api_url : `${p}@@${ep.url}@@${ep?.token}` : ep.url;

    urlArr.push(`${ep.name.replace(/\b0+(?=[1-9])/g, '')}$${urls}`); // 拼接格式：集名$链接，推入临时数组
  }

  play_url.push(urlArr.join('#')); // 单线路所有集用#分隔，存入总数组
}

var vod = {
    "type_name": res.vod.vod_class,
    "vod_year": res.vod.vod_year,
    "vod_area": res.vod.vod_area,
    "vod_remarks": res.vod.vod_remarks,
    "vod_actor": "",
    "vod_director": "",
    "vod_content": res.vod.vod_content,
    "vod_play_from": play_from, 
    "vod_play_url": play_url.join('$$$')
    }

return JSON.stringify({ list: [vod] })
}


//播放
async function play (flag, id, flags) {
//
if (id.indexOf("@@") > -1){
let [ parse_api, url, token ] = id.split("@@");
let res = (await request(`${host}/api.php/getappapi.index/vodParse`, `parse_api=${parse_api}&url=${encodeURIComponent(en(url))}&token=${token || ""}`)).json;

return JSON.stringify({ parse: 0, url: JSON.parse(res).url });
}
//
if (/http.*url=.*m3u8|url=http.*m3u8|url.*http.*m3u8|\?url=/i.test (id)) {
let url = JSON.parse((await req(id)).content).url
return JSON.stringify ({parse: 0,url: url})
}

return JSON.stringify ({parse: 0, url: id});
}

//搜索
async function search (wd, quick, pg=1) {
let res = (await request(`${host}/api.php/getappapi.index/searchList`, `keywords=${wd}&type_id=0&page=${pg}`)).search_list
let videos = res.map(item => ({
    vod_id: item.vod_id,
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