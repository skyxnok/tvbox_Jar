

let host, playinfo;


async function init(cfg) {
let resp = await request("https://xmino.oss-cn-beijing.aliyuncs.com/xmino.json");
host = resp.endpoints[0];
let login = await request(`${host}/api/auth/login-password`, {
    "phone": "13544125511",
    "password": "100200300"
});
hh.authorization = "Bearer " + login.data.token.access_token;
let players = await request(`${host}/api/players`);
playinfo = {};
for (const key in players.data) {
    playinfo[key] = players.data[key].name;
}
}

let hh = {
  "user-agent": "Mozilla/5.0 (Linux; Android 11) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36",
  "x-device-model": "23116PN5BC",
  "x-device-name": "Xiaomi",
  "x-device-id": "mobile_mszo6gs7_avcian",
  "content-type": "application/json",
  "x-device-type": "android",
  "x-platform-sig": "zbZOM5jVxeequ4uwwVqreb1hnJAMfJEqfosu6YhbesFvMkHeM347l/qXYp3TcpH4jPGKxcR5cBfDH1dL3PmPAw==",
  "x-platform": "android"
}

//统一请求
async function request(reqUrl, body) {
    const opt = { headers: hh };
    if (body && typeof body === 'object') {
        opt.body = JSON.stringify(body);
        opt.method = 'POST';
    }
    let resp = (await req(reqUrl, opt)).content;
    return JSON.parse(resp);
}


//分类
async function home (filter) {
let html = await request(`${host}/api/categories`)
let banners = await request(`${host}/api/home/banners`)

let classes = html.data.map(tp => ({
  type_id: tp.type_id,
  type_name: tp.type_name
}))

let filterObj = {};
for (const tp of html.data) {
  let extend = {};
  try { if (tp.type_extend) extend = JSON.parse(tp.type_extend); } catch(e) {}
  let values = [];
  if (extend.class && extend.class.length) {
    values.push({
      "key": "class",
      "name": "分类",
      "value": extend.class.map(c => ({"n": c, "v": c}))
    });
  }
  values.push({
    "key": "sort",
    "name": "排序",
    "value": [{"n": "最热", "v": "hits"},{"n": "最新", "v": "time"},{"n": "评分", "v": "score"}]
  });
  filterObj[tp.type_id] = values;
}

let videos = banners.data.map(item => ({
    vod_id: item.vod_id,
    vod_name: item.vod_name,
    vod_pic: item.vod_pic,
    vod_remarks: item.vod_class,
    vod_year: item.vod_year
}));

return JSON.stringify({ class: classes, filters: filterObj, list: videos });
}


//主页推荐
async function homeVod() {
}

//分类
async function category (tid, pg, filter, extend) {
let html = await request(`${host}/api/videos?page=${pg}&limit=18&sort=${extend.sort || 'hits'}&t=${tid}&class=${extend.class || ''}`)

let videos = html.data.list.map(item => ({
    vod_id: item.vod_id,
    vod_name: item.vod_name,
    vod_pic: item.vod_pic,
    vod_remarks: item.vod_remarks,
    vod_year: item.vod_year
}));

return JSON.stringify({ page: pg, pagecount: 99999, limit: videos.length, total: 99999, list: videos });
}

//详情
async function detail (id) {
let res = (await request(`${host}/api/videos/${id}`)).data
let play_from = res.play_list.map(item => `${playinfo[item.from] || item.from} [${item.from}]`).join('$$$')

let play_url = []; // 存放所有线路拼接完成后的字符串
let playlistLen = res.play_list.length; // 缓存线路总长度，减少重复读取
for (let i = 0; i < playlistLen; i++) { // 外层循环：遍历每条播放线路
  let playItem = res.play_list[i]; // 当前循环到的单条线路对象
  let p = playItem.from; // 提取线路解析模板字符串
  let epList = playItem.episodes; // 当前线路下全部剧集数组
  
  let urlArr = []; // 临时数组，存本条线路每一集拼接结果
  let epLen = epList.length; // 缓存当前线路剧集总数

  for (let j = 0; j < epLen; j++) { // 内层循环：遍历当前线路每一集
    let ep = epList[j]; // 取出单集信息对象
    urlArr.push(`${ep.name}$${p}@@${ep.url}@@${id}`); // 拼接格式：集名$链接，推入临时数组
  }

  play_url.push(urlArr.join('#')); // 单线路所有集用#分隔，存入总数组
}

var vod = {
    "type_name": res.vod_tag,
    "vod_year": res.vod_year,
    "vod_area": res.vod_area,
    "vod_remarks": res.vod_remarks,
    "vod_actor": res.vod_actor,
    "vod_director": res.vod_director,
    "vod_content": res.vod_content.replace(/<.*?>/g, ''),
    "vod_play_from": play_from,
    "vod_play_url": play_url.join('$$$')
}

return JSON.stringify({ list: [vod] })
}


//播放
async function play (flag, id, flags) {
let [from, urls, vod_id] = id.split('@@')


let url = (await request(`${host}/api/parse`, {
  "from": from,
  "url": urls,
  "vod_id": Number(vod_id)
})).data.url


return JSON.stringify({ parse: 0,url: url })
}

//搜索
async function search (wd, quick, pg=1) {
let html = await request(`${host}/api/search?wd=${wd}&page=${pg}&limit=20`)

let videos = html.data.list.map(item => ({
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