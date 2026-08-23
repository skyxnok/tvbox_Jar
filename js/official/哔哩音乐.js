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
let classes = [{"type_name":"MV合集","type_id":"MV合集"},{"type_name":"MV4K","type_id":"MV4K合集"},{"type_name":"新年歌曲","type_id":"新年歌曲"},{"type_name":"舞曲","type_id":"舞曲超清合集"},{"type_name":"演唱会","type_id":"演唱会超清"},{"type_name":"经典老歌","type_id":"经典老歌合集"},{"type_name":"抖音热歌","type_id":"抖音热歌"},{"type_name":"国语金曲","type_id":"国语歌曲合集"},{"type_name":"粤语金曲","type_id":"粤语歌曲合集"},{"type_name":"2025年热榜","type_id":"2025年歌曲合集"},{"type_name":"一人一首","type_id":"一人一首成名曲"},{"type_name":"车载DJ","type_id":"DJ合集"},{"type_name":"欧美金曲","type_id":"英语歌曲合集"},{"type_name":"日韩合集","type_id":"日韩歌曲合集"},{"type_name":"古风歌曲","type_id":"古风歌曲"},{"type_name":"国乐大典","type_id":"国乐合集"},{"type_name":"禅修音乐","type_id":"禅修音乐"},{"type_name": "白噪音","type_id": "白噪音超清"}]

//二级
let filterObj = {"演唱会超清":[{"key":"mx","name":"分类","value":[{"n":"Beyond","v":"Beyond"},{"n":"By2","v":"By2"},{"n":"B坂井泉水","v":"坂井泉水"},{"n":"C陈奕迅","v":"陈奕迅"},{"n":"C蔡依林","v":"蔡依林"},{"n":"C初音未来","v":"初音未来"},{"n":"C蔡健雅","v":"蔡健雅"},{"n":"C陈小春","v":"陈小春"},{"n":"C草蜢","v":"草蜢"},{"n":"C陈慧娴","v":"陈慧娴"},{"n":"C崔健","v":"崔健"},{"n":"D戴荃","v":"戴荃"},{"n":"D动力火车","v":"动力火车"},{"n":"D邓丽君","v":"邓丽君"},{"n":"D丁当","v":"丁当"},{"n":"D刀郎","v":"刀郎"},{"n":"D邓紫棋","v":"邓紫棋"},{"n":"D戴佩妮","v":"戴佩妮"},{"n":"D邓丽君","v":"邓丽君"},{"n":"F飞儿乐队","v":"飞儿乐队"},{"n":"F费玉清","v":"费玉清"},{"n":"F费翔","v":"费翔"},{"n":"F方大同","v":"方大同"},{"n":"F凤凰传奇","v":"凤凰传奇"},{"n":"G郭采洁","v":"郭采洁"},{"n":"G光良","v":"光良"},{"n":"G郭富城","v":"郭富城"},{"n":"H胡彦斌","v":"胡彦斌"},{"n":"H韩红","v":"韩红"},{"n":"H黄品源","v":"黄品源"},{"n":"H黄霄云","v":"黄霄云"},{"n":"H黄小琥","v":"黄小琥"},{"n":"H花儿乐队","v":"花儿乐队"},{"n":"H黄家强","v":"黄家强"},{"n":"H后街男孩","v":"后街男孩"},{"n":"J经典老歌","v":"经典老歌"},{"n":"J贾斯丁比伯","v":"贾斯丁比伯"},{"n":"J金池","v":"金池"},{"n":"J金志文","v":"金志文"},{"n":"J焦迈奇","v":"焦迈奇"},{"n":"K筷子兄弟","v":"筷子兄弟"},{"n":"L李玟","v":"李玟"},{"n":"L林忆莲","v":"林忆莲"},{"n":"L李克勤","v":"李克勤"},{"n":"L刘宪华","v":"刘宪华"},{"n":"L李圣杰","v":"李圣杰"},{"n":"L林宥嘉","v":"林宥嘉"},{"n":"L梁静茹","v":"梁静茹"},{"n":"L李健","v":"李健"},{"n":"L林俊杰","v":"林俊杰"},{"n":"L李玉刚","v":"李玉刚"},{"n":"L林志炫","v":"林志炫"},{"n":"L李荣浩","v":"李荣浩"},{"n":"L李宇春","v":"李宇春"},{"n":"L洛天依","v":"洛天依"},{"n":"L林子祥","v":"林子祥"},{"n":"L李宗盛","v":"李宗盛"},{"n":"L黎明","v":"黎明"},{"n":"L刘德华","v":"刘德华"},{"n":"L罗大佑","v":"罗大佑"},{"n":"L林肯公园","v":"林肯公园"},{"n":"LLadyGaga","v":"LadyGaga"},{"n":"L旅行团乐队","v":"旅行团乐队"},{"n":"M莫文蔚","v":"莫文蔚"},{"n":"M毛不易","v":"毛不易"},{"n":"M梅艳芳","v":"梅艳芳"},{"n":"M迈克尔杰克逊","v":"迈克尔杰克逊"},{"n":"N南拳妈妈","v":"南拳妈妈"},{"n":"P朴树","v":"朴树"},{"n":"Q齐秦","v":"齐秦"},{"n":"Q青鸟飞鱼","v":"青鸟飞鱼"},{"n":"R容祖儿","v":"容祖儿"},{"n":"R任贤齐","v":"任贤齐"},{"n":"S水木年华","v":"水木年华"},{"n":"S孙燕姿","v":"孙燕姿"},{"n":"S苏打绿","v":"苏打绿"},{"n":"SSHE","v":"SHE"},{"n":"S孙楠","v":"孙楠"},{"n":"T陶喆","v":"陶喆"},{"n":"T谭咏麟","v":"谭咏麟"},{"n":"T田馥甄","v":"田馥甄"},{"n":"T谭维维","v":"谭维维"},{"n":"T逃跑计划","v":"逃跑计划"},{"n":"T田震","v":"田震"},{"n":"T谭晶","v":"谭晶"},{"n":"T屠洪刚","v":"屠洪刚"},{"n":"T泰勒·斯威夫特","v":"泰勒·斯威夫特"},{"n":"W王力宏","v":"王力宏"},{"n":"W王杰","v":"王杰"},{"n":"W吴克群","v":"吴克群"},{"n":"W王心凌","v":"王心凌"},{"n":"W王靖雯","v":"好声音王靖雯"},{"n":"W汪峰","v":"汪峰"},{"n":"W伍佰","v":"伍佰"},{"n":"W王菲","v":"王菲"},{"n":"W五月天","v":"五月天"},{"n":"W汪苏泷","v":"汪苏泷"},{"n":"X徐佳莹","v":"徐佳莹"},{"n":"X弦子","v":"弦子"},{"n":"X萧亚轩","v":"萧亚轩"},{"n":"X许巍","v":"许巍"},{"n":"X薛之谦","v":"薛之谦"},{"n":"X许嵩","v":"许嵩"},{"n":"X小虎队","v":"小虎队"},{"n":"X萧敬腾","v":"萧敬腾"},{"n":"X谢霆锋","v":"谢霆锋"},{"n":"X徐小凤","v":"徐小凤"},{"n":"X信乐队","v":"信乐队"},{"n":"Y夜愿乐队","v":"夜愿乐队"},{"n":"Y羽泉","v":"羽泉"},{"n":"Y郁可唯","v":"郁可唯"},{"n":"Y叶倩文","v":"叶倩文"},{"n":"Y杨坤","v":"杨坤"},{"n":"Y庾澄庆","v":"庾澄庆"},{"n":"Y尤长靖","v":"尤长靖"},{"n":"Y易烊千玺","v":"易烊千玺"},{"n":"Y袁娅维","v":"袁娅维"},{"n":"Y杨丞琳","v":"杨丞琳"},{"n":"Y杨千嬅","v":"杨千嬅"},{"n":"Y杨宗纬","v":"杨宗纬"},{"n":"Z郑秀文","v":"郑秀文"},{"n":"Z周杰伦","v":"周杰伦"},{"n":"Z张学友","v":"张学友"},{"n":"Z张信哲","v":"张信哲"},{"n":"Z张宇","v":"张宇"},{"n":"Z周华健","v":"周华健"},{"n":"Z张韶涵","v":"张韶涵"},{"n":"Z周深","v":"周深"},{"n":"Z纵贯线","v":"纵贯线"},{"n":"Z赵雷","v":"赵雷"},{"n":"Z周传雄","v":"周传雄"},{"n":"Z张国荣","v":"张国荣"},{"n":"Z周慧敏","v":"周慧敏"},{"n":"Z张惠妹","v":"张惠妹"},{"n":"Z周笔畅","v":"周笔畅"},{"n":"Z郑中基","v":"郑中基"},{"n":"Z张艺兴","v":"张艺兴"},{"n":"Z张震岳","v":"张震岳"},{"n":"Z张雨生","v":"张雨生"},{"n":"Z郑智化","v":"郑智化"},{"n":"Z卓依婷","v":"卓依婷"}]}]}


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