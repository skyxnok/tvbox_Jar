//https://www.mutefun.cc/  https://www.2kdm.com

let host = 'https://go.5idm.top';

async function init(cfg) {
}

//解密
function de(data) {
let key = 'b04089bdeffe24ccea1df4ed16205e23';
return aesX('AES/ECB/PKCS5', false, data, true, key, null, false);
}

//请求处理函数
async function request(reqUrl, body) {
  const opt = { headers: {"Content-Type": "application/json","User-Agent": "Dart/3.5 (dart:io)"} };
  if (body && typeof body === 'object') {
    opt.body = JSON.stringify(body);
    opt.method = 'POST';
  }
  const res = JSON.parse((await req(reqUrl, opt)).content).data;
  return JSON.parse(de(res));
}

//分类
async function home (filter) {
let html = await request(`${host}/app/api/config?platform=android`);

//一级
let classes = html.ac_vod_type.map(tp => ({
  type_id: tp.type_id,
  type_name: tp.type_name
}))

//二级
let filterObj = {};
for (const type of html.ac_vod_type) {
  if (type.type_id) {
    const filters = [];
    const classValues = type.type_extend.class.split(',').map(v => ({ n: v, v })).filter(v => v.v);
    if (classValues.length > 0) {
      filters.push({ key: 'class', name: '类型', value: classValues });
    }

    const yearValues = type.type_extend.year.split(',').map(v => ({ n: v, v })).filter(v => v.v);
    if (yearValues.length > 0) {
      filters.push({ key: 'year', name: '年份', value: yearValues });
    }
    filters.push({ key: 'sort', name: '排序', value: [{"n":"最新","v":"0"},{"n":"热度","v":"1"},{"n":"好评","v":"2"}] })
    filterObj[type.type_id] = filters;
    
  }
}

return JSON.stringify({ class: classes, filters: filterObj});
}


//主页推荐
async function homeVod() {
}

//分类
async function category (tid, pg, filter, extend) {
let html = await request(`${host}/app/api/content/filter?type=${tid}&page=${pg}&sort=${extend.sort || '0'}&year=${extend.year || ''}&class=${extend.class || ''}`);

let videos = html.filter_vods.map(item => ({
    vod_id: item.id,
    vod_name: item.vod_name,
    vod_pic: `${item.vod_pic}@Referer=${item.vod_pic}`,
    vod_remarks: item.vod_remarks,
    // vod_year: item.vod_year
}));

return JSON.stringify({ page: pg, pagecount: 99999, limit: videos.length, total: 99999, list: videos });
}

//详情
async function detail (id) {
let html = await request(`${host}/app/api/vod/${id}`);

let play_from = html.playerData.map(item => `${item.name} [${item.player}]` ).join('$$$');
let play_url = html.playerData.map(play => {
  let player = play.player;
  return play.vids.map(vid => `${vid}@@${player}`).join(`#`);
}).join('$$$');

var vod = {
//    "type_name": html.vod_class,
    "vod_year": html.vod_year,
//    "vod_area": html.other,
    "vod_remarks": html.vod_remarks,
    "vod_actor": "",
    "vod_director": "",
    "vod_content": html.vod_content.replace(/<.*?>/g, ''),
    "vod_play_from": play_from.replace(/-首次加载缓慢请耐心等待/g,''), 
    "vod_play_url": play_url
    }

return JSON.stringify({ list: [vod] })
}


//播放
async function play (flag, id, flags) {
let [ vid, player] = id.split("@@");
let data = await request(`${host}/app/api/vod/parse`, {"vid":vid,"player":player} );
let url = data.play_url;

return JSON.stringify({parse: 0,url: url})
}

//搜索
async function search (wd, quick, pg=1) {
let html = await request(`${host}/app/api/search/full?q=${wd}`);

let videos = html.search_full.map(item => ({
    vod_id: item.id,
    vod_name: item.vod_name,
    vod_pic: `${item.vod_pic}@Referer=${item.vod_pic}`,
    vod_remarks: item.vod_remarks,
    vod_year: item.vod_year
}));

return JSON.stringify({limit: videos.length, list: videos});
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