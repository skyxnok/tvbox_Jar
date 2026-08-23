//import "assets://js/lib/crypto-js.js";

async function init(cfg) {
}

//https://manshan.fun/
let host = 'https://app.manshan.fun'

//解密
function de(data) {
    const clean = data.replace(/\"/g,'');
    const key = 'zhuhongleipeipei';
    return aesX('AES/ECB/PKCS5', false, clean, true, key, null, false);
}

//请求
async function request (data, path) {
let t = Math.floor(Date.now() / 1000);
let sign = md5X(`${t}${path}zhl's river app`, true).replace(/\=/g,'').replace(/\+/g,'-').replace(/\//g,'_')
let urls = data.includes('?')  ? `&sign=${sign}&time=${t}` : `?sign=${sign}&time=${t}`
let res = (await req(`${data}${urls}`, { headers:{"user-agent": "Dart/3.11 (dart:io)"} } )).content
return JSON.parse(de(res))
}

//分类
async function home (filter) {
let path = `/app/tab/getList`
let html = await request(`${host}/app/tab/getList`,path)

let classes = html.data.map(tp => ({
  type_id: tp.id,
  type_name: tp.title
}))
classes.push({
  type_id: '片库',
  type_name: '片库'
});

let path2 = `/app/category/getList`
let html2 = await request(`${host}/app/category/getList`,path2)

let filterObj = {"片库":[
    {
      "key": "type",
      "name": "类型",
      "value": html2.data[1].values.map(i=>({n:i,v:i})) || []
    },
    {
      "key": "class",
      "name": "剧情",
      "value": html2.data[2].values.map(i=>({n:i,v:i})) || []
    },
    {
      "key": "year",
      "name": "年份",
      "value": html2.data[3].values.map(i=>({n:i,v:i})) || []
    },
    {
      "key": "sort",
      "name": "排序",
      "value": html2.data[0].values.map(i=>({n:i,v:i})) || []
    }
  ]}

return JSON.stringify({ class: classes, filters: filterObj });
}


//主页推荐
async function homeVod() {
}

//分类
async function category (tid, pg, filter, extend) {
if (tid === '片库') {
  let path = `/app/category/getVideoList`
  let html = await request(`${host}/app/category/getVideoList?sort=${extend.sort || '最热'}&category=${extend.type || '全部'}&genres=${extend.class || '全部'}&year=${extend.year || '全部'}&pageNo=${pg}&pageSize=21`, path)
  let videos = html.data.map(item => ({
    vod_id: item.id,
    vod_name: item.title,
    vod_pic: `${item.pic}@Referer=https://douban.com@User-Agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36`,
    vod_remarks: item.remarks,
    vod_year: item.year
  }));
  return JSON.stringify ({
    page: pg,
    pagecount: 99999,
    limit: videos.length,
    total: 99999,
    list: videos
  });
}else{
  let path = `/app/video/getList`
  let html = await request(`${host}/app/video/getList?tabId=${tid}`, path)
  let videos = html.data.map(i => {
    return i.videoList.map(item => ({
      vod_id: item.id,
      vod_name: item.title,
      vod_pic: `${item.pic}@Referer=https://douban.com@User-Agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36`,
      vod_remarks: item.remarks,
      vod_year: item.year
    }));
  }).flat();
  return JSON.stringify ({
    page: 1,
    pagecount: 1,
    limit: videos.length,
    total: 1,
    list: videos
  });
}
}

//详情
async function detail (id) {
let path = `/app/video/getDetail`
let html = (await request(`${host}/app/video/getDetail?videoId=${id}`, path)).data
let title = html.title
let play_url;

if (html.douBanType === 'movie') {
  play_url = html.episodeList.map(i => `${i.title}$${title}@@${i.id}@@0001`).join('#');
} else {
  play_url = html.episodeList.map((i, index) => {
    const seqNum = (index + 1).toString().padStart(4, '0');
    return `${i.title}$${title}@@${i.id}@@${seqNum}`;
  }).join('#');
}

var vod = {
    "type_name": html.genres,
    "vod_year": html.year,
    "vod_area": html.area,
    "vod_remarks": html.remarks,
    "vod_actor": html.actor,
    "vod_director": html.director,
    "vod_content": html.description,
    "vod_play_from": '漫闪',
    "vod_play_url": play_url
    }

return JSON.stringify({ list: [vod] })
}


//播放
async function play (flag, id, flags) {
let ids = id.split('@@')
let path = `/app/episode/jx`
let html = await request(`${host}/app/episode/jx?videoTitle=${ids[0]}&episodeId=${ids[1]}`, path)

let hh = html.data.playHeader
let url = html.data.resolutionList.flatMap(i=>[i.name.replace(/super/g,'超清').replace(/high/g,'高清').replace(/low/g,'标清') , i.url ]);
return JSON.stringify({
  parse: 0,
  url: url,
  header: hh ? JSON.stringify(hh) : ''
})
}

//搜索
async function search (wd, quick, pg=1) {
let path = `/app/video/search`
let html = await request(`${host}/app/video/search?keyWord=${wd}`, path)

let videos = html.data.map(item => ({
  vod_id: item.id,
  vod_name: item.title,
  vod_pic: `${item.pic}@Referer=https://douban.com@User-Agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36`,
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