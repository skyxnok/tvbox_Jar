//import "assets://js/lib/crypto-js.js";

//bite5.top
//let host = 'https://sh11.fannaz.top';

let host;
let playkey;
let playinfo;

async function init(cfg) {
host = JSON.parse(AES1((await req('https://shdm-1319164733.cos.ap-nanjing.myqcloud.com/api.txt')).content))[0];
let info = JSON.parse(AES2((await req(`${host}/shark/api.php?action=configs`, {
    body: `username=&token=`,
    headers: hh(),
    method: 'POST'
})).content));
playkey = info.config.hulue.split('&')[0]
playinfo = info.playerinfos
}

//解密host
function AES1(data) {
    const clean = data.replace(/<.*>|\n/g, '').trim();
    const key = 'rectangleadsadxa';
    return aesX('AES/ECB/PKCS5', false, clean, true, key, null, false);
}


//解密内容
function AES2(data) {
    const clean = data.replace(/<.*>|\n/g, '').trim(); 
    const key = 'aassddwwxxllsx1x';
    return aesX('AES/ECB/PKCS5', false, clean, true, key, null, false);
}

//播放解析
function AES3(data) {
    const clean = data.replace(/<.*>|\n/g, '').trim(); 
    const key = playkey;
    return aesX('AES/ECB/PKCS5', false, clean, true, key, null, false);
}

//请求头
function hh() {
return {"user-agent": "Dalvik/1.4.0 (Linux; U; Android 9; Xiaomi Build/23116PN5BC)","version": "1.4.0","content-type": "application/x-www-form-urlencoded;charset=UTF-8"}
}

//播放请求头
function playhh() {
return {"Icy-MetaData": "1","allowCrossProtocolRedirects": "true","Accept-Encoding": "identity","User-Agent": "Dalvik/1.4.0 (Linux; U; Android 9; Xiaomi Build/23116PN5BC)","Connection": "Keep-Alive"}
}

//分类
async function home (filter) {

//一级
let classes = [{"type_id":"1","type_name":"日漫"},{"type_id":"2","type_name":"国漫"},{"type_id":"3","type_name":"剧场"},{"type_id":"4","type_name":"番剧"}]

//二级
let filterObj = {"1":[{key:"year",name:"年份",value:[{"v":"全部年代","n":"全部"},{"v":"2026","n":"2026"},{"v":"2025","n":"2025"},{"v":"2024","n":"2024"},{"v":"2023","n":"2023"},{"v":"2022","n":"2022"},{"v":"2021","n":"2021"},{"v":"2020","n":"2020"},{"v":"2019","n":"2019"},{"v":"2018","n":"2018"},{"v":"2017","n":"2017"},{"v":"2016","n":"2016"},{"v":"2015","n":"2015"},{"v":"2014","n":"2014"},{"v":"2013","n":"2013"},{"v":"2012","n":"2012"},{"v":"2011","n":"2011"}],},{key:"sort",name:"排序",value:[{"v":"最新","n":"最新"},{"v":"最热","n":"最热"},{"v":"最赞","n":"最赞"}],}],"2":[{key:"year",name:"年份",value:[{"v":"全部年代","n":"全部"},{"v":"2026","n":"2026"},{"v":"2025","n":"2025"},{"v":"2024","n":"2024"},{"v":"2023","n":"2023"},{"v":"2022","n":"2022"},{"v":"2021","n":"2021"},{"v":"2020","n":"2020"},{"v":"2019","n":"2019"},{"v":"2018","n":"2018"},{"v":"2017","n":"2017"},{"v":"2016","n":"2016"},{"v":"2015","n":"2015"},{"v":"2014","n":"2014"},{"v":"2013","n":"2013"},{"v":"2012","n":"2012"},{"v":"2011","n":"2011"}],},{key:"sort",name:"排序",value:[{"v":"最新","n":"最新"},{"v":"最热","n":"最热"},{"v":"最赞","n":"最赞"}],}],"3":[{key:"year",name:"年份",value:[{"v":"全部年代","n":"全部"},{"v":"2026","n":"2026"},{"v":"2025","n":"2025"},{"v":"2024","n":"2024"},{"v":"2023","n":"2023"},{"v":"2022","n":"2022"},{"v":"2021","n":"2021"},{"v":"2020","n":"2020"},{"v":"2019","n":"2019"},{"v":"2018","n":"2018"},{"v":"2017","n":"2017"},{"v":"2016","n":"2016"},{"v":"2015","n":"2015"},{"v":"2014","n":"2014"},{"v":"2013","n":"2013"},{"v":"2012","n":"2012"},{"v":"2011","n":"2011"}],},{key:"sort",name:"排序",value:[{"v":"最新","n":"最新"},{"v":"最热","n":"最热"},{"v":"最赞","n":"最赞"}],}],"4":[{key:"year",name:"年份",value:[{"v":"全部年代","n":"全部"},{"v":"2026","n":"2026"},{"v":"2025","n":"2025"},{"v":"2024","n":"2024"},{"v":"2023","n":"2023"},{"v":"2022","n":"2022"},{"v":"2021","n":"2021"},{"v":"2020","n":"2020"},{"v":"2019","n":"2019"},{"v":"2018","n":"2018"},{"v":"2017","n":"2017"},{"v":"2016","n":"2016"},{"v":"2015","n":"2015"},{"v":"2014","n":"2014"},{"v":"2013","n":"2013"},{"v":"2012","n":"2012"},{"v":"2011","n":"2011"}],},{key:"sort",name:"排序",value:[{"v":"最新","n":"最新"},{"v":"最热","n":"最热"},{"v":"最赞","n":"最赞"}],}]}

let html = JSON.parse(AES2((await req(`${host}/api.php/v1.rank/RankData?page=1&type_id=2`, {headers: hh()})).content))
//推荐
let videos = html.data.videos.map(item => ({
    vod_id: item.vod_id,
    vod_name: item.vod_name,
    vod_pic: item.vod_pic,
    vod_year: item.vod_year
}));


return JSON.stringify({ class: classes, filters: filterObj, list: videos });
}


//主页推荐
async function homeVod() {
}

//分类
async function category (tid, pg, filter, extend) {
let html = JSON.parse(AES2((await req(`${host}/api.php/v1.classify/content?page=${pg}`, {
    body: JSON.stringify({
        area: "全部地区",
        lang: "全部语言",
        type: "全部类型",
        year: extend.year || '全部年代',
        type_id: tid,
        rank: extend.sort || '最新'
    }),
    headers: {"user-agent": "Dalvik/1.4.0 (Linux; U; Android 9; Xiaomi Build/23116PN5BC)","version": "1.4.0","content-type": "application/json; charset=utf-8"},
    method: 'POST'
})).content))

let videos = html.data.video_list.map(item => ({
    vod_id: item.vod_id,
    vod_name: item.vod_name,
    vod_pic: item.vod_pic,
    vod_remarks: item.vod_score + '分'
}));

return JSON.stringify({ page: pg, pagecount: 99999, limit: videos.length, total: 99999, list: videos });
}

//详情
async function detail (id) {
let html = JSON.parse(AES2((await req(`${host}/api.php/v1.player/details?vod_id=${id}`, {headers: hh()})).content))

  let res = html.data.detail;
  
  let play_from = res.play_url_list.map(item => item.from).join('$$$');
  let play_url = res.play_url_list.map(play => {
    let from = play.from; // 获取 from
    return play.urls.map(item => {
//        let name = item.name;
//        let url = item.url;
        return `${item.name}$${from}@@${item.url}`; 

    }).join('#');
}).join('$$$');

var vod = {
    "type_name": res.vod_class,
    "vod_year": res.vod_year,
    "vod_remarks": res.vod_remarks,
    "vod_actor": "",
    "vod_director": "",
    "vod_content": res.vod_content,
    "vod_play_from": play_from.replace(/dyttm3u8/,'天堂云[稳定]').replace(/lmm/,'路漫漫[全面]').replace(/dmbs/,'动漫巴士[全面]').replace(/CYDD1/,'樱花云[原画]').replace(/dxt/,'猫盘[电信]').replace(/ndx/,'猫盘[简中]').replace(/dbz/,'猫盘[极速]').replace(/tkk/,'AA-04[备用]').replace(/tkk/,'AA-04[备用]').replace(/7se/,'七色番[简中]').replace(/aafun2/,'BB-02[New线]').replace(/aafun1/,'BB-03[New线]').replace(/iyf/,'爱一帆').replace(/aowu/,'嗷呜[New线]'),
    "vod_play_url": play_url
    }

return JSON.stringify({ list: [vod] })
}


//播放
async function play (flag, id, flags) {
let ids = id.split('@@')
let plays = playinfo.find(p => p.playername === ids[0]);
let phh = playhh();
phh.referer = (plays.playerua.match(/referer:(.*?)(;|$)/) || [])[1]?.trim() || '';

if (plays) {
    if (plays.playerjiekou) {
    let html = JSON.parse(AES2((await req(`${host}/shark/api.php?action=parsevod`, {
    body: `parse=${encodeURIComponent(AES3(plays.playerjiekou))}&url=${encodeURIComponent(ids[1])}&matching=`,
    headers: hh(),
    method: 'POST'
    })).content)).url
    return JSON.stringify({parse: 0,url: html, header:phh})
    }else{
    return JSON.stringify({parse: 0,url: ids[1], header:phh})
    }
    }else{
    return JSON.stringify({parse: 0,url: ids[1], header:phh})
    }

}

//搜索
async function search (wd, quick, pg=1) {

let html = JSON.parse(AES2((await req(`${host}/api.php/v1.search/data?wd=${wd}&type_id=0&page=${pg}`, {headers: hh()})).content))

let videos = html.data.search_data.map(item => ({
    vod_id: item.vod_id,
    vod_name: item.vod_name,
    vod_pic: item.vod_pic,
    vod_remarks: item.vod_remarks
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

