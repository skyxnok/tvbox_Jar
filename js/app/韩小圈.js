

let host = 'https://hxqapi.hiyun.tv';

async function init(cfg) {
let res = await req(`${host}/api/common/configs`,{
        headers: gethh()
    })
}

//解密响应体
function de(data) {
    let items = JSON.parse(data);
    let inner = md5X(`ikk1Kuq1E4T018TUnSQ6${items.ts}`);
    let md5Str = md5X(`${inner}34F9Q53w/HJW8E6Q`);
    let key = md5Str.substring(0,16);
    let iv = md5Str.substring(16);
    let dec = aesX("AES/CBC/PKCS5", false, items.data, true, key, iv, false);
    return JSON.parse(dec)
}

//加密请求体
function en(data) {
  return aesX("AES/CBC/PKCS5", true, data, false, 'a9fc04840498848e', '3cb63eec5e162717', true); //uid md5后是 key iv
}

//解密playurl
function deplay(data) {
    let items = JSON.parse(data);
    let inner = md5X(`ikk1Kuq1E4T018TUnSQ6`);
    let md5Str = md5X(`${inner}34F9Q53w/HJW8E6Q`);
    let key = md5Str.substring(0,16);
    let iv = md5Str.substring(16);
    let dec = aesX("AES/CBC/PKCS5", false, items.datas[0].data, true, key, iv, false);
    return JSON.parse(dec)
}

//请求头
function gethh(aps) {
let t = Date.now();
let data = JSON.stringify({
  "emu":0,"ou":0,"it":t,"iit":t,"bs":0,"uid":"ikk1Kuq1E4T018TUnSQ6",
  "pc":0,"tm":60,"d8m":"0,0,0,0,0,0,0,0","md":"23113RKC6C","maker":"Redmi",
  "osv":"9","br":-2147483648,"rpc":0,"scc":1,"plc":0,"toc":9,"tsc":1,"ts":t,
  "pa":1,"nw":2,"px":"0","isp":"","ai":"b7ca10733358e7ca","ii":"","dpc":0,
  "dsc":0,"qpc":0,"apad":0,"pk":"com.babycloud.hanju"
})
let sign = aesX("AES/CBC/PKCS5", true, data, false, 'a9fc04840498848e', '3cb63eec5e162717', true);

return {
    "vc": "a_8110",
    "vn": "6.6.5",
    "ch": "xiaomi",
    "app": "hj",
    "User-Agent": "HanjuTV/6.6.5 (23113RKC6C; Android 9; Scale/2.00)",
    "said": "68babf529c1e02ba",
    "uk": "T98Aa/zIMX3qoHOqvCgJwCOkl8Fvqa2GOrSBz/g37YE=", //AES加密uid, key:f349wghhe784tqwh, iv:d3w8hf94fidk38lk 
    "auth-token": "",
    "auth-uid": "",
    "sign": sign
}
}

//生成随机字符串
function getRandStr(len) {
  var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz';
  var str = '';
  for (var i = 0; i < len; i++) {
    str += chars[Math.floor(Math.random() * 62)];
  }
  return str;
}

//代理
async function proxy(params) {
let res1 = de((await req(`${host}/api/series2/episode/detail?pid=${params.pid}`,{
        headers: gethh()
    })).content)
let uuid = getRandStr(32)
let pid = res1.playItem.pid
let scid = res1.playItem.sources[0].scid

let traceId = de((await req(`${host}/api/carp/reward/v2?scene=ad_series_play`,{
        headers: gethh()
    })).content).traceId

let bodydata = en(`{"pid":"${pid}","scene":"ad_series_play","traceId":"${traceId}"}`)
let headers = gethh();
let aps = md5X(`{"data":"${bodydata}"}GIpxY0JPylRx`).toLowerCase()
headers["Content-Type"] = 'application/json; charset=UTF-8'
headers["aps"] = aps

let ttk = de((await req(`${host}/api/carp/reward/rp/v2`,{
        body: JSON.stringify({"data":bodydata}),
        headers: headers,
        method: 'POST'
    })).content).rewardTokenInfo.token

let t = Math.floor(Date.now() / 1000)
let sign = md5X(`&version=6.6.5&uuid=${uuid}&udid=a9fc04840498848e3cb63eec5e162717&ttk=${ttk}&t=${t}&sq=${params.sq}&scid=${scid}&re=1&pid=${pid}&dt=android&2E159Q/Z8979WckQ`)
let play = deplay((await req(`${host}/api/series/rslvV4?t=${t}&dt=android&version=6.6.5&uuid=${uuid}&pid=${pid}&scid=${scid}&sq=${params.sq}&re=1&ttk=${ttk}&sign=${sign}`,{
        headers: gethh()
    })).content)

let a = '.ts?' + play.playUrl.split('?')[1]
let b = 'https://' + play.playUrl.split('/')[2]
let m3u8Content = (await req(play.playUrl,{
        headers: play.header
    })).content.replace(/^\/.+\.ts$/gm, (path) => {
    return b + path; 
  }).replace(/\.ts/g, a);
return [200, "application/x-mpegurl", m3u8Content];
}

//分类
async function home (filter) {
let html = de((await req(`${host}/api/series2/arrange/cate?stype=1`,{
        headers: gethh()
    })).content)

let classes = html.groups.map(tp => ({
  type_id: tp.stype,
  type_name: tp.name
}))

let filterObj = {};
html.groups.forEach(group => {
  filterObj[group.stype] = [
    {
      "key": "class",
      "name": "剧情",
      "value": group.cates.map(i => ({n: i.name, v: i.value || i.name})) || []
    },
    {
      "key": "year",
      "name": "年份", 
      "value": html.years.map(i => ({n: i.name, v: i.value}))
    },
    {
      "key": "sort",
      "name": "排序",
      "value": html.sorts.map(i => ({n: i.name, v: i.value}))
    }
  ];
});

let tj = de((await req(`${host}/api/index/recommend_v5?page=1`,{
        headers: gethh()
    })).content)

//推荐
let videos = tj.mediaBlocks[0].seriesList.map(item => ({
    vod_id: item.sid,
    vod_name: item.name,
    vod_pic: item.image.thumb || item.image.poster,
    vod_remarks: item.detailMemo,
    vod_year: item.shorthand
}));

return JSON.stringify({ class: classes, filters: filterObj, list: videos });
}

//主页推荐
async function homeVod() {
}

//分类
async function category (tid, pg, filter, extend) {
let html = de((await req(`${host}/api/series2/arrange	/cate?stype=${tid}&sort=${extend.sort || 'hot'}&year=${extend.year || '-1'}&cid=-1&page=${pg}`,{
        headers: gethh()
    })).content)

let videos = html.seriesList.map(item => ({
    vod_id: item.sid,
    vod_name: item.name,
    vod_pic: item.image.thumb,
    vod_remarks: item.detailMemo,
    vod_year: item.shorthand
}));

return JSON.stringify({ page: pg, pagecount: 99999, limit: videos.length, total: 99999, list: videos });
}

//详情
async function detail (id) {
let html = de((await req(`${host}/api/series2/detail/normal?sid=${id}`,{
        headers: gethh()
    })).content)

let qualities = html.series.scopeQualities || [];
let playItems = html.playItems || [];
let play_from = qualities.map(q => `${q.name}${q.resolution}`).join('$$$');
let play_url = qualities.map(q =>
    playItems.map(play => `${play.serialNo}$${play.pid}@@${q.value}`).join('#')
).join('$$$');

var vod = {
    "type_name": '',
    "vod_year": '',
    "vod_area": '',
    "vod_remarks": html.series.detailMemo,
    "vod_actor": html.series.crew.replace(/.+?:|.+?：/,''),
    "vod_director": html.series.shorthand,
    "vod_content": html.series.intro.replace(/<.+?>|&nbsp;/g,' '),
    "vod_play_from": play_from, 
    "vod_play_url": play_url
    }

return JSON.stringify({ list: [vod] })
}


//播放
async function play (flag, id, flags) {
let [pid, sq] = id.split('@@')
let url = `${getProxy(true)}&pid=${pid}&sq=${sq}`
return JSON.stringify({ parse: 0,url: url })
}

//搜索
async function search (wd, quick, pg=1) {
let html = de((await req(`${host}/api/search/s5?k=${wd}&srefer=search_input&type=2&page=${pg}`,{
        headers: gethh()
    })).content)
let videos = html.seriesList.map(item => ({
    vod_id: item.sid,
    vod_name: item.name,
    vod_pic: item.image.thumb,
    vod_remarks: item.detailMemo,
    vod_year: item.shorthand
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
      search: search,
      proxy: proxy
  };
}