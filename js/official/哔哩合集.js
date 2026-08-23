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
    "referer": "https://search.bilibili.com"
  }
})
return JSON.parse(res.content)
}

//转换时间
function gettime(s) {
  if (!s || !s.includes(':')) return '';
  const [m, s_] = s.split(':').map(x => parseInt(x, 10) || 0);
  return m < 60 
    ? `${('0' + m).slice(-2)}:${('0' + s_).slice(-2)}` 
    : `${~~(m/60)}:${('0' + (m%60)).slice(-2)}:${('0' + s_).slice(-2)}`;
}

//分类
async function home (filter) {

//一级
let classes = [{"type_name":"沙雕动漫","type_id":"一口气沙雕动漫"},{"type_name":"纪录片","type_id":"纪录片超清"},{"type_name":"演唱会","type_id":"演唱会超清"},{"type_name":"风景","type_id":"风景4K"},{"type_name":"说案","type_id":"说案"},{"type_name":"鬼畜","type_id":"鬼畜"},{"type_name":"搞笑","type_id":"搞笑超清"},{"type_name":"儿童","type_id":"儿童超清"},{"type_name":"动物世界","type_id":"动物世界超清"},{"type_name":"相声小品","type_id":"相声小品超清"},{"type_name":"音乐","type_id":"音乐"}]

//二级
let filterObj = {}

return JSON.stringify({ class: classes, filters: filterObj });
}


//主页推荐
async function homeVod() {
}

//分类
async function category (tid, pg, filter, extend) {
let html = await request(`${host}/x/web-interface/search/type?search_type=video&keyword=${tid}&page=${pg||1}`)

let videos = html.data.result.map(item => ({
    vod_id: item.bvid,
    vod_name: item.title.replace(/<[^>]*>?/gm, ''),
    vod_pic: item.pic.includes('http') ? item.pic : 'https:' + item.pic,
    vod_remarks: gettime(item.duration) || '',
    style: { "type": "rect", "ratio": 1.485 }
}));


return JSON.stringify({ page: pg, pagecount: 99999, limit: videos.length, total: 99999, list: videos });
}

//详情
async function detail (id) {
let html = await request(`${host}/x/web-interface/view?bvid=${id}`)

let play_url = html.data.pages.map(item => {
    return `${item.part.replace(/#/g,'')}$${id}@@${item.cid}`
}).join('#');

var vod = {
    "vod_director": html.data.owner.name,
    "vod_content": html.data.desc,
    "vod_play_from": '哔哩', 
    "vod_play_url": play_url
    }

return JSON.stringify({ list: [vod] })
}


//播放
async function play (flag, id, flags) {
let [bvid, cid] = id.split('@@')
let html = await request(`${host}/x/player/wbi/playurl?bvid=${bvid}&cid=${cid}&dm_cover_img_str=QzxoLUY%2FL1lGWjM1RlgpTWhXKEJAXz5dXENzL0g3fUl%2BQlVkfTd3LlxcK3JHb1gtdSZgSDopWFM0NDhET19je2RcL3t8T2ZWK3FSXS82dFgpaDd0Ll1rZ1QxMUJSc3YwKW54dDFOQSx5O3RDOUR8fHxLQ0ZZc3NFd2FdWSw%2BSA&dm_img_inter=%7B%22ds%22%3A%5B%5D%2C%22wh%22%3A%5B0%2C0%2C0%5D%2C%22of%22%3A%5B0%2C0%2C0%5D%7D&dm_img_list=%5B%5D&dm_img_str=Jz93SzgwQU1kaUl1QE93VTEvaTdUKj85QmNQWk&fnval=4048&fnver=0&fourk=1&gaia_source=pre-load&isGaiaAvoided=true&qn=127&try_look=1&voice_balance=0&web_location=1315873`)
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

// 与 Java 类似：每个清晰度对应一个短 proxy URL，proxy 复用缓存的 playinfo 生成多音视轨 MPD
let url = videos.flatMap(v => [qualityMapping[v.id] || v.id, `${dlurl}&bvid=${bvid}&cid=${cid}&qn=${v.id}`]);


return JSON.stringify({
        header:{
    "cookie": cookies,
    "user-agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36",
    "referer": "https://www.bilibili.com"
  },
        parse: 0,
        url: url,
        danmaku: `https://api.bilibili.com/x/v1/dm/list.so?oid=${cid}`,
        format:"application/dash+xml"
});



}

//搜索
async function search (wd, quick, pg=1) {
let html = await request(`${host}/x/web-interface/search/type?search_type=video&keyword=${wd}&page=${pg}`)

let videos = html.data.result.map(item => ({
    vod_id: item.bvid,
    vod_name: item.title.replace(/<[^>]*>?/gm, ''),
    vod_pic: item.pic.includes('http') ? item.pic : 'https:' + item.pic,
    vod_remarks: gettime(item.duration) || '',
    style: { "type": "rect", "ratio": 1.485 }
}));


return JSON.stringify({ limit: videos.length, list: videos });
}


async function proxy(params) {
let bvid = params.bvid
let cid = params.cid
let qn = params.qn

// 优先复用 play 缓存的 playinfo，避免重复请求；兜底（直接访问 proxy 时）再请求一次
let html = playinfo;
if (!html || !html.data || !html.data.dash) {
    html = await request(`${host}/x/player/wbi/playurl?bvid=${bvid}&cid=${cid}&dm_cover_img_str=QzxoLUY%2FL1lGWjM1RlgpTWhXKEJAXz5dXENzL0g3fUl%2BQlVkfTd3LlxcK3JHb1gtdSZgSDopWFM0NDhET19je2RcL3t8T2ZWK3FSXS82dFgpaDd0Ll1rZ1QxMUJSc3YwKW54dDFOQSx5O3RDOUR8fHxLQ0ZZc3NFd2FdWSw%2BSA&dm_img_inter=%7B%22ds%22%3A%5B%5D%2C%22wh%22%3A%5B0%2C0%2C0%5D%2C%22of%22%3A%5B0%2C0%2C0%5D%7D&dm_img_list=%5B%5D&dm_img_str=Jz93SzgwQU1kaUl1QE93VTEvaTdUKj85QmNQWk&fnval=4048&fnver=0&fourk=1&gaia_source=pre-load&isGaiaAvoided=true&qn=127&try_look=1&voice_balance=0&web_location=1315873`)
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