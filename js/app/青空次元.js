//https://www.sorani.org

let host = 'https://api.sorani.cc';

async function request(reqUrl, body) {
  const opt = { headers: {"Origin": "https://www.sorani.net","user-agent": "Dart/3.11 (dart:io)","accept": "application/json","x-sorani-app-version": "1.0.3+4","content-type": "application/json","x-sorani-guest-key": "e2ae63b73b544dc09a1f10e046d5c9a9","x-sorani-device-id": "sorani-Z4QUqszCGnGcodfzqm7-_mBRhDiJVQdU"} };
  if (body && typeof body === 'object') {
    opt.body = JSON.stringify(body);
    opt.method = 'POST';
    opt.headers['Content-Type'] = 'application/json';
  }
  const res = JSON.parse((await req(reqUrl, opt)).content);
  return res
}

//分类
async function home (filter) {
let html = await request(`${host}/sorani-cms/api/video/home-page?platform=2`);
//一级
let classes = html.data.categories.map(tp => ({
  type_id: tp.id,
  type_name: tp.name
}))

//推荐
let videos = html.data.banners.map(item => ({
    vod_id: item.contentId,
    vod_name: item.title,
    vod_pic: item.contentCover
}));

return JSON.stringify({ class: classes, filters: {}, list: videos });
}

//推荐
async function homeVod() {

}

//分类
async function category (tid, pg, filter, extend) {
let html = await request(`${host}/sorani-cms/api/video?page=${pg}&size=20&enabled=true&sortMode=latest&sortDesc=true&categoryId=${tid}`);

let videos = html.data.records.map(item => ({
    vod_id: item.id,
    vod_name: item.title,
    vod_pic: item.cover,
    vod_remarks: item.statusText,
    vod_year: item.year
}));

return JSON.stringify({ page: pg, pagecount: 99999, limit: videos.length, total: 99999, list: videos });
}

//详情
async function detail (id) {
let html = (await request(`${host}/sorani-cms/api/video/${id}/play-page`)).data;
let list = [];
const sources = html.playLines;
const episodes = html.episodes;

for (let i of sources) {
    let items = episodes.map(ep => `${ep.title}$${i.code}@@${ep.episodeId}`).join('#')
    list.push({
        play_from: `${i.name} [${i.code}]`,
        play_url: items
    });
}

let res = html.detail
var vod = {
    "type_name": res.tags,
    "vod_year": res.year,
    "vod_area": res.area,
    "vod_remarks": res.statusText,
    "vod_actor": "",
    "vod_director": res.director,
    "vod_content": res.summary.replace(/<.*?>/g, ''),
    "vod_play_from": list.map(item => item.play_from).join('$$$'),
    "vod_play_url": list.map(item => item.play_url).join('$$$')
    }

return JSON.stringify({ list: [vod] })
}

//播放
async function play (flag, id, flags) {
let [code, ids] = id.split('@@');

let url = (await request(`${host}/sorani-cms/api/video/episode/${ids}/play?lineCode=${code}`)).data.playUrl;

return JSON.stringify({parse: 0, url: url})
}

//搜索
async function search (wd, quick, pg=1) {
let html = await request(`${host}/sorani-cms/api/video/search?keyword=${wd}&sortMode=relevance_popular&limit=20&offset=0`);

let videos = html.data.map(item => ({
    vod_id: item.id,
    vod_name: item.title,
    vod_pic: item.cover,
    vod_remarks: item.statusText,
    vod_year: item.year
}));

return JSON.stringify({limit: videos.length, list: videos});
}

export function __jsEvalReturn() {
  return {
//      init: init,
      home: home,
      homeVod: homeVod,
      category: category,
      detail: detail,
      play: play,
      search: search
  };
}

