


let host = 'https://api.bilibili.com';
let cookies;
let playinfo;

async function init(cfg) {
let res = (await req(`https://www.bilibili.com`,{
  headers: {"user-agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36"}
})).headers['set-cookie'][0]?.split(';')[0];
cookies = res ? res : 'buvid3=522AB244-3E11-43A5-8C46-62934836C10240947infoc'
}



async function request(reqUrl) {
let res = await req(reqUrl, {
  headers: {
    "cookie": cookies,
    "user-agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36",
    "referer": "https://www.bilibili.com"
  }
})
return JSON.parse(res.content)
}

// 解密函数
function aesDecrypt(data) {
const datas = data.replace(/[^a-zA-Z0-9+/=:]|\"|\\|https:/g,'').split(':');
const key = datas[1];
const iv  = datas[2];
//模式  加密  内容  内容是不是b64  key  iv   输出b64
return aesX('AES/CBC/PKCS5', false, datas[4], true, key, iv, false);
}

//分类
async function home (filter) {

//一级
let classes = [{"type_name":"番剧","type_id":"1"},{"type_name":"国创","type_id":"4"},{"type_name":"剧集","type_id":"5"},{"type_name":"综艺","type_id":"7"},{"type_name":"纪录","type_id":"3"}]

//二级
let filterObj = {"2":[{"key":"order","name":"排序","value":[{"n":"播放数量","v":"2"},{"n":"更新时间","v":"0"},{"n":"最高评分","v":"4"},{"n":"弹幕数量","v":"1"},{"n":"追看人数","v":"3"},{"n":"开播时间","v":"5"},{"n":"上映时间","v":"6"}]}],"5":[{"key":"order","name":"排序","value":[{"n":"播放数量","v":"2"},{"n":"更新时间","v":"0"},{"n":"最高评分","v":"4"},{"n":"弹幕数量","v":"1"},{"n":"追看人数","v":"3"},{"n":"开播时间","v":"5"},{"n":"上映时间","v":"6"}]}],"7":[{"key":"order","name":"排序","value":[{"n":"播放数量","v":"2"},{"n":"更新时间","v":"0"},{"n":"最高评分","v":"4"},{"n":"弹幕数量","v":"1"},{"n":"追看人数","v":"3"},{"n":"开播时间","v":"5"},{"n":"上映时间","v":"6"}]}],"3":[{"key":"order","name":"排序","value":[{"n":"播放数量","v":"2"},{"n":"更新时间","v":"0"},{"n":"最高评分","v":"4"},{"n":"弹幕数量","v":"1"},{"n":"追看人数","v":"3"},{"n":"开播时间","v":"5"},{"n":"上映时间","v":"6"}]}],"1":[{"key":"order","name":"排序","value":[{"n":"播放数量","v":"2"},{"n":"更新时间","v":"0"},{"n":"最高评分","v":"4"},{"n":"弹幕数量","v":"1"},{"n":"追看人数","v":"3"},{"n":"开播时间","v":"5"},{"n":"上映时间","v":"6"}]}],"4":[{"key":"order","name":"排序","value":[{"n":"播放数量","v":"2"},{"n":"更新时间","v":"0"},{"n":"最高评分","v":"4"},{"n":"弹幕数量","v":"1"},{"n":"追看人数","v":"3"},{"n":"开播时间","v":"5"},{"n":"上映时间","v":"6"}]}]}

let html = await request(`${host}/pgc/season/index/result?order=4&area=-1&style_id=-1&season_version=-1&season_status=-1&spoken_language_type=-1&copyright=-1&is_finish=-1&year=-1&season_month=-1&season_type=1&type=0&page=1&pagesize=21`)

let videos = html.data.list.map(item => ({
    vod_id: item.season_id,
    vod_name: item.title,
    vod_pic: item.cover,
    vod_remarks: item.index_show,
    vod_year: item.order
}));

return JSON.stringify({ class: classes, filters: filterObj, list: videos });
}


//主页推荐
async function homeVod() {
}

//分类
async function category (tid, pg, filter, extend) {
let html = await request(`${host}/pgc/season/index/result?order=${extend.order || '3'}&season_status=-1&style_id=-1&sort=-1&area=-1&pagesize=20&type=1&season_type=${tid}&page=${pg}`)

let videos = html.data.list.map(item => ({
    vod_id: item.season_id,
    vod_name: item.title,
    vod_pic: item.cover,
    vod_remarks: item.index_show,
    vod_year: item.order
}));


return JSON.stringify({ page: pg, pagecount: 99999, limit: videos.length, total: 99999, list: videos });
}

//详情
async function detail (id) {
let html = (await request(`${host}/pgc/view/web/season?season_id=${id}`)).result

let play_url = html.episodes.filter(item => item.badge !== "预告").map(item => {
    return `${item.show_title}$${item.bvid}@@${item.ep_id}@@${item.id}@@${item.cid}@@${item.link}@@${item.badge}`
}).join('#');

var vod = {
    "type_name": html.styles.join('\n') || '',
    "vod_year": html.publish?.pub_time?.split('-')[0] || '',
    "vod_area": html.areas.name || '',
    "vod_remarks": html.new_ep.desc || '',
    "vod_actor": html.actors,
    "vod_director": html.staff,
    "vod_content": html.evaluate,
    "vod_play_from": '哔哩', 
    "vod_play_url": play_url
    }

return JSON.stringify({ list: [vod] })
}


//播放
async function play (flag, id, flags) {
let phh = { 
    "cookie": cookies, 
    "user-agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36", 
    "referer": "https://www.bilibili.com" 
}
let [bvid, ep_id, season_id, cid, link, badge] = id.split('@@')

//官方
if (badge !== '会员'){
try {
let html = await request(`${host}/x/player/wbi/playurl?bvid=${bvid}&ep_id=${ep_id}&season_id=${season_id}&cid=${cid}&qn=127&fnval=4048&fourk=1&fnver=0&voice_balance=0&gaia_source=pre-load&isGaiaAvoided=true&web_location=1315873&try_look=1&dm_img_list=%5B%5D&dm_img_str=Jz93SzgwQU1kaUl1QE93VTEvaTdUKj85QmNQWk&dm_cover_img_str=QzxoLUY%2FL1lGWjM1RlgpTWhXKEJAXz5dXENzL0g3fUl%2BQlVkfTd3LlxcK3JHb1gtdSZgSDopWFM0NDhET19je2RcL3t8T2ZWK3FSXS82dFgpaDd0Ll1rZ1QxMUJSc3YwKW54dDFOQSx5O3RDOUR8fHxLQ0ZZc3NFd2FdWSw%2BSA&dm_img_inter=%7B%22ds%22%3A%5B%5D%2C%22wh%22%3A%5B0%2C0%2C0%5D%2C%22of%22%3A%5B0%2C0%2C0%5D%7D`)
playinfo = html // 缓存，proxy 直接复用，避免重复请求

// 清晰度映射
const qualityMapping = {};
for (let i = 0; i < html.data.accept_quality.length; i++) {
    qualityMapping[html.data.accept_quality[i]] = html.data.accept_description[i];
}
// 按 accept_quality 顺序筛选所有可用视频轨
let videos = [];
for (const qn of html.data.accept_quality) {
    const found = html.data.dash.video.find(item => String(item.id) === String(qn));
    if (found) videos.push(found);
}

let dlurl = getProxy(true)

let url = videos.flatMap(v => [qualityMapping[v.id] || v.id, `${dlurl}&bvid=${bvid}&ep_id=${ep_id}&season_id=${season_id}&cid=${cid}&qn=${v.id}`]);


return JSON.stringify({
    header:phh,
    parse: 0,
    url: url,
    danmaku: `https://api.bilibili.com/x/v1/dm/list.so?oid=${cid}`,
    format:"application/dash+xml"
});
} catch (e) {}
}


//至尊4K 公众号夕颜工作室
try {
let jx1 = JSON.parse((await req(`http://zz2.mftv.top/api/index?parsesId=3&appid=10004&videoUrl=${link}`,{headers:{
    "token": "fdfb1079-e6e2-46a4-ac6d-f88778faf455",
    "User-Agent": "okhttp/4.12.0"
}})).content)
if (jx1.code=='200'){
return JSON.stringify({
    header:phh,
    parse: 0,
    url: aesDecrypt(jx1.url),
    danmaku: `https://api.bilibili.com/x/v1/dm/list.so?oid=${cid}`
});
}
} catch (e) {}


return JSON.stringify({ parse: 0, url: link, jx: 1, danmaku: `https://api.bilibili.com/x/v1/dm/list.so?oid=${cid}` });
}

//搜索
async function search (wd, quick, pg=1) {
let sshh = {
    "cookie": cookies,
    "user-agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36",
    "referer": "https://search.bilibili.com"
}
//剧场版
let html = JSON.parse((await req(`${host}/x/web-interface/search/type?search_type=media_ft&keyword=${wd}&page=${pg}`,{headers:sshh})).content)
//番剧
let html2 = JSON.parse((await req(`${host}/x/web-interface/search/type?search_type=media_bangumi&keyword=${wd}&page=${pg||1}`,{headers:sshh})).content)

let videos = [
    ...(html?.data?.result || []).map(item => ({
        vod_id: item.season_id,
        vod_name: item.title.replace(/<.+?>/g,''),
        vod_pic: item.cover,
        vod_remarks: item.index_show
    })),
    ...(html2?.data?.result || []).map(item => ({
        vod_id: item.season_id,
        vod_name: item.title.replace(/<.+?>/g,''),
        vod_pic: item.cover,
        vod_remarks: item.index_show
    }))
];


return JSON.stringify({ limit: videos.length, list: videos });
}


async function proxy(params) {
let bvid = params.bvid
let cid = params.cid
let qn = params.qn
let ep_id = params.ep_id
let season_id = params.season_id

// 优先复用 play 缓存的 playinfo，避免重复请求；兜底（直接访问 proxy 时）再请求一次
let html = playinfo;
if (!html || !html.data || !html.data.dash) {
    html = await request(`${host}/x/player/wbi/playurl?bvid=${bvid}&ep_id=${ep_id}&season_id=${season_id}&cid=${cid}&qn=127&fnval=4048&fourk=1&fnver=0&voice_balance=0&gaia_source=pre-load&isGaiaAvoided=true&web_location=1315873&try_look=1&dm_img_list=%5B%5D&dm_img_str=Jz93SzgwQU1kaUl1QE93VTEvaTdUKj85QmNQWk&dm_cover_img_str=QzxoLUY%2FL1lGWjM1RlgpTWhXKEJAXz5dXENzL0g3fUl%2BQlVkfTd3LlxcK3JHb1gtdSZgSDopWFM0NDhET19je2RcL3t8T2ZWK3FSXS82dFgpaDd0Ll1rZ1QxMUJSc3YwKW54dDFOQSx5O3RDOUR8fHxLQ0ZZc3NFd2FdWSw%2BSA&dm_img_inter=%7B%22ds%22%3A%5B%5D%2C%22wh%22%3A%5B0%2C0%2C0%5D%2C%22of%22%3A%5B0%2C0%2C0%5D%7D`)
}
let dash = html.data.dash
let duration = dash.duration

// 视频轨：按传入的 qn 过滤，只返回该清晰度下所有 codecid 的 Representation
// 每个 codecid 一个 AdaptationSet，ExoPlayer 会显示为 3 条视轨（H.264/H.265/AV1）
let videoXml = '';
let codecSeen = new Set();
for (const video of dash.video) {
    if (String(video.id) !== String(qn)) continue;
    if (codecSeen.has(video.codecid)) continue;
    codecSeen.add(video.codecid);
    // 收集该 qn + codecid 下的视频（通常只有一条，但兼容多条）
    let group = dash.video.filter(item => String(item.id) === String(qn) && item.codecid === video.codecid);
    let xml = `<AdaptationSet>\n<ContentComponent contentType="video"/>\n`;
    for (const v of group) {
        const u = v.baseUrl.replace(/&/g, '&amp;');
        const id = v.id + '_' + v.codecid;
        xml += `<Representation id="${id}" bandwidth="${v.bandwidth}" codecs="${v.codecs}" mimeType="video/mp4" height='${v.height}' width='${v.width}' frameRate='${v.frameRate}' sar='${v.sar}' startWithSAP="${v.startWithSap}">\n` +
               `<BaseURL>${u}</BaseURL>\n` +
               `<SegmentBase indexRange="${v.SegmentBase.indexRange}">\n<Initialization range="${v.SegmentBase.Initialization}"/></SegmentBase>\n` +
               `</Representation>\n`;
    }
    xml += '</AdaptationSet>\n';
    videoXml += xml;
}

// 音频轨：每个 Representation 单独一个 AdaptationSet，对应 Java 多音轨
const audioOrder = { "30280": "192000", "30232": "132000", "30216": "64000" };
let audioXml = '';
for (const key of Object.keys(audioOrder)) {
    let audio = dash.audio.find(item => String(item.id) === key);
    if (audio) {
        const u = audio.baseUrl.replace(/&/g, '&amp;');
        const id = audio.id + '_' + audio.codecid;
        audioXml += `<AdaptationSet>\n<ContentComponent contentType="audio"/>\n` +
                    `<Representation id="${id}" bandwidth="${audio.bandwidth}" codecs="${audio.codecs}" mimeType="audio/mp4" numChannels='2' sampleRate='${audioOrder[key]}' startWithSAP="${audio.startWithSap}">\n` +
                    `<BaseURL>${u}</BaseURL>\n` +
                    `<SegmentBase indexRange="${audio.SegmentBase.indexRange}">\n<Initialization range="${audio.SegmentBase.Initialization}"/></SegmentBase>\n` +
                    `</Representation>\n</AdaptationSet>\n`;
    }
}

let playUrl = `<?xml version="1.0" encoding="UTF-8"?>
<MPD xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
     xmlns="urn:mpeg:dash:schema:mpd:2011" 
     xsi:schemaLocation="urn:mpeg:dash:schema:mpd:2011 DASH-MPD.xsd" 
     type="static" 
     mediaPresentationDuration="PT${duration}S" 
     minBufferTime="PT5S" 
     profiles="urn:mpeg:dash:profile:isoff-on-demand:2011">
  <Period duration="PT${duration}S" start="PT0S">
    ${videoXml}
    ${audioXml}
  </Period>
</MPD>`;

  return [200, "application/dash+xml", playUrl];
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