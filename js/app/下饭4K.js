//http://juxiafan.com/

async function init(cfg) {
}

let host = 'http://194.147.100.155:7744'

//统一请求
async function request(reqUrl, body) {
    const jsonHead = {"User-Agent": "okhttp/5.3.2","Content-Type": "application/json;charset=utf-8"};
    const formHead = {"Content-Type": "application/x-www-form-urlencoded","User-Agent": "okhttp/5.3.2"};
    const opt = {
        headers: (typeof body === 'string' && body.length > 0) ? formHead : jsonHead,
        method: 'POST'
    };
    if (body !== undefined) {
        opt.body = (typeof body === 'object') ? JSON.stringify(body) : body;
    }
    return JSON.parse((await req(reqUrl, opt)).content);
}

//加密
function en(data) {
    return aesX("AES/ECB/PKCS5", true, data, false, 'kZ6fT8oF6oM8eX6lF7eH2rJ3pW7gW0kC', null, true);
}

//去掉采集线路
function filterUrls(data) {
const exclude = ['bfzym3u8', 'tym3u8', 'zjm3u8', 'lzm3u8', 'sdm3u8', 'kbm3u8', 'bjm3u8', 'xkm3u8', 'tpm3u8', 'hnm3u8', 'wjm3u8', 'ffm3u8', '99m3u8', 'dbm3u8', 'rym3u8', 'mzm3u8', 'mym3u8', 'wwm3u8', 'mtm3u8', 'modum3u8', '360zy'];

return data.filter(item => !exclude.includes(item.sourceCode));
}

//分类
async function home (filter) {
let html = (await request(`${host}/api/v1/video/classifies`, '')).data.sort((a, b) => a.sort - b.sort)

//一级
let classes = html.map(tp => ({
  type_id: tp.id,
  type_name: tp.name
}))

//二级
let filterObj = html.reduce((acc, type) => {
    acc[type.id] = [
      { 
        key: 'class', 
        name: '类型', 
        value: type.extend.class.split(',').map(v => ({ n: v, v })) 
      },
      { 
        key: 'area', 
        name: '地区', 
        value: type.extend.area.split(',').map(v => ({ n: v, v })) 
      },
      { 
        key: 'year', 
        name: '年份', 
        value: type.extend.year.split(',').map(v => ({ n: v, v })) 
      }
    ];
  return acc;
}, {});

return JSON.stringify({ class: classes, filters: filterObj });
}


//主页推荐
async function homeVod() {
}

//分类
async function category (tid, pg, filter, extend) {
let t = Math.floor(Date.now() / 1000);
let html = await request(`${host}/api/v1/video/index`, {
      "area": extend.area || '',
      "classify": extend.class || '',
      "pageNum": pg,
      "pageSize": 40,
      "typeId": tid,
      "year": extend.year || '',
      "timestamp": t,
      "datasign": en(`pageNum=${pg}&pageSize=40&timestamp=${t}&typeId=${tid}`)
    })

let videos = html.data.list.map(item => ({
    vod_id: item.id,
    vod_name: item.name,
    vod_pic: item.videoPic,
    vod_remarks: item.remarks,
    vod_year: item.year
}));

return JSON.stringify({ page: pg, pagecount: 99999, limit: videos.length, total: 99999, list: videos });
}

//详情
async function detail (id) {
let t = Math.floor(Date.now() / 1000);
let html = (await request(`${host}/api/v1/video/videoDetails`, `datasign=${encodeURIComponent(en(`id=${id}&timestamp=${t}`))}&id=${id}&timestamp=${t}`)).data

let playlist = filterUrls(html.playerSource)

let play_from = playlist.map(item => `${item.sourceName}(${item.sourceCode})`).join('$$$');
let play_url = playlist.map(play => {
    let p = play.parseUrl;
    let code = play.sourceCode;
    return play.episodes.map(item => {
        let urls = p ? `${p}${item.playerCode}` : `${code}@@${item.playerCode}`
        return `${item.episodeName}$${urls}`; 
    }).join('#');
}).join('$$$');

var vod = {
    "type_name": html.classify,
    "vod_year": html.year,
    "vod_area": html.area,
    "vod_remarks": html.remarks,
    "vod_actor": html.actor,
    "vod_director": html.director,
    "vod_content": html.content,
    "vod_play_from": play_from, 
    "vod_play_url": play_url
    }

return JSON.stringify({ list: [vod] })
}


//播放
async function play (flag, id, flags) {
//
if (id.indexOf("m3u8") > -1){
let url = id.includes('@@') ? id.split('@@')[1] : id
return JSON.stringify ({parse: 0,url: url})
}
//
if (id.indexOf("@@") > -1){
let ids = id.split('@@')
let t = Math.floor(Date.now() / 1000);
let sign = encodeURIComponent(en(`code=${ids[1]}&from=${ids[0]}&timestamp=${t}`))
let url = (await request(`${host}/api/v1/player/analysisUrl`, `code=${ids[1]}&datasign=${sign}&from=${ids[0]}&timestamp=${t}`)).data
return JSON.stringify ({parse: 0,url: url})
}
//
if (id.indexOf("url=") > -1){
let url = JSON.parse((await req(id)).content).url
return JSON.stringify ({parse: 0,url: url})
}

return JSON.stringify ({parse: 0,url: id})
}

//搜索
async function search (wd, quick, pg=1) {
let t = Math.floor(Date.now() / 1000);
let html = await request(`${host}/api/v1/video/search`, {
      "keyword": wd,
      "pageNum": pg,
      "pageSize": 40,
      "timestamp": t,
      "datasign": en(`keyword=${wd}&pageNum=${pg}&pageSize=40&timestamp=${t}`)
    })

let videos = html.data.list.map(item => ({
    vod_id: item.id,
    vod_name: item.name,
    vod_pic: item.videoPic,
    vod_remarks: item.remarks,
    vod_year: item.year
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