//import "assets://js/lib/crypto-js.js";

async function init(cfg) {
}

let host = 'https://pbaccess.video.qq.com';


//请求函数
async function request(reqUrl, body) {
const hh = {
    "accept": "application/json",
    "user-agent": "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36",
    "content-type": "application/json",
    "origin": "https://v.qq.com",
    "sec-fetch-site": "same-site",
    "sec-fetch-mode": "cors",
    "sec-fetch-dest": "empty",
    "referer": "https://v.qq.com/"
}
const res = JSON.parse((await req(reqUrl, {
    body: JSON.stringify(body),
    headers: hh,
    method: 'POST'
})).content);
return res;
}

//解析剧集列表
function getlist(result) {
    //正片
    let data = result.data.module_list_datas[0].module_datas[0].item_data_lists.item_datas.map(i => i.item_params).filter(item => item.is_trailer === "0");
    let zp = [];
    for (const item of data) {
        // 筛选有效数据（含cid和vid）
        if (item.cid && item.vid) {
            zp.push(`${item.title}$https://v.qq.com/x/cover/${item.cid}/${item.vid}.html`);
        }
    }
    return zp
}

//解密1
function aesDecrypt(data) {
const datas = data.split("https://baidu.con/")[1];
const key = datas.slice(0, 16);
const iv  = datas.slice(0, 16);
//模式  加密  内容  内容是不是b64  key  iv   输出b64
return aesX('AES/CBC/PKCS5', false, datas.slice(16), true, key, iv, false);
}

//畅影视界解析 蜂蜜9盒子
function 畅影解析(data) {
const datas = data.replace(/[^a-zA-Z0-9+/=:]|\"|\\|https:/g,'').split(':');
const key = datas[1];
const iv  = datas[2];
//模式  加密  内容  内容是不是b64  key  iv   输出b64
return aesX('AES/CBC/PKCS5', false, datas[4], true, key, iv, false);
}

//b64解码
function b64Decode(s) {
    const t='ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/';
    s=s.replace(/\s/g,'');
    const padLen = (4 - s.length % 4) % 4;
    s += '='.repeat(padLen);
    let o=[],b=0,n=0;
    for(let c of s){
        let v=t.indexOf(c);
        if(v<0)continue;
        b=b<<6|v;n+=6;
        if(n>=8){n-=8;o.push(b>>n&255)}
    }
    if(padLen === 1) o.pop();
    if(padLen === 2) o.pop(),o.pop();
    return o.map(x=>String.fromCharCode(x)).join('');
}

//雨鹿解析
function 雨鹿解析(data) {
let datas = b64Decode(data).split("https://ldmax.cooom/")[1]
let key = datas.slice(0, 16).split('').reverse().join('')
return aesX('AES/CBC/PKCS5', false, datas.slice(16), true, key, key, false);
}

//本地代理弹幕接口
async function proxy(params) {
  let vid = params.url.split('/').pop().split('.')[0];
  const headers = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
    "Referer": "https://v.qq.com/"
  };
  // 1. 获取分段索引
  const baseRes = JSON.parse((await req(`https://dm.video.qq.com/barrage/base/${vid}`, { headers: headers })).content);
  const segmentIndex = baseRes.segment_index || {};
  const segments = Object.values(segmentIndex).map(item => `https://dm.video.qq.com/barrage/segment/${vid}/${item.segment_name}`);
  if (segments.length === 0) {
    return [200, "application/xml", '<i></i>'];
  }
  // 2. 并发获取所有分段弹幕
  const tasks = segments.map(segUrl =>
    http(segUrl, { headers: headers }).then(r => JSON.parse(r.content)).catch(() => null)
  );
  const segResults = await Promise.all(tasks);
  // 3. 合并弹幕
  let barrageList = [];
  for (const seg of segResults) {
    if (seg && Array.isArray(seg.barrage_list)) barrageList.push(...seg.barrage_list);
  }
  const xml = getXML(barrageList);
  return [200, "application/xml", xml];
}

//转XML弹幕格式
function getXML(barrageList) {
  const xmlParts = ['<i>\n']; // 使用数组存储片段
  if (!Array.isArray(barrageList)) return '<i></i>';

  for (const item of barrageList) {
    const time = (Number(item.time_offset) / 1000).toFixed(2);
    const content = item.content || '';
    // 解析样式（position / color / font_size）
    let style = {};
    try { style = JSON.parse(item.content_style || '{}'); } catch (e) {}
    // position: 1=滚动(right) 2=顶部 3=底部(高级)
    const position = style.position;
    const mode = position === 2 ? 5 : position === 3 ? 4 : 1;
    // 颜色：优先渐变色，其次普通色
    const colorHex = (style.gradient_colors && style.gradient_colors[0]) || style.color || '#FFFFFF';
    const colorDecimal = parseInt(String(colorHex).replace(/^#/, ''), 16) || 16777215;
    // 字号
    const fontSizeRaw = style.font_size || item.font_size || 24;
    const fontSize = String(fontSizeRaw).replace('px', '');
    // 内容清洗：转义XML特殊字符
    const safeContent = content
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
    if (!safeContent) continue;
    xmlParts.push(`    <d p="${time},${mode},${fontSize},${colorDecimal}">${safeContent}</d>\n`);
  }
  xmlParts.push('</i>');
  return xmlParts.join(''); // 一次性拼接
}

//分类
async function home (filter) {
//一级
let classes = [{"type_id":"100113","type_name":"剧集"},{"type_id":"100173","type_name":"电影"},{"type_id":"100119","type_name":"动漫"},{"type_id":"100109","type_name":"综艺"},{"type_id":"100105","type_name":"纪录"},{"type_id":"100150","type_name":"少儿"},{"type_id":"110755","type_name":"短剧"}];
//二级
let filterObj = {
"100113":[{"key":"sort","name":"排序","value":[{"v":"75","n":"最热"},{"v":"79","n":"最新"},{"v":"85","n":"高分"}]},{"key":"area","name":"地区","value":[
{"n":"地区","v":"-1"},
{"n":"中国","v":"0"},
{"n":"中国香港","v":"14"},
{"n":"中国台湾","v":"4"},
{"n":"美国","v":"8"},
{"n":"韩国","v":"5"},
{"n":"日本","v":"10"}
]}],

"100173":[{"key":"sort","name":"排序","value":[{"v":"75","n":"最热"},{"n":"最新","v":"83"},{"n":"高分","v":"81"}]},{"key":"area","name":"地区","value":[
{"n":"地区","v":"-1"},
{"n":"中国","v":"100024"},
{"n":"中国香港","v":"100025"},
{"n":"中国台湾","v":"100026"},
{"n":"美国","v":"100029"},
{"n":"韩国","v":"100028"},
{"n":"日本","v":"100027"}
]}],

"100119":[{"key":"sort","name":"排序","value":[{"v":"75","n":"最热"},{"v":"23","n":"更新"},{"v":"85","n":"高分"}]},{"key":"area","name":"地区","value":[
{"n":"地区","v":"-1"},
{"n":"国漫","v":"1"},
{"n":"日漫","v":"2"},
{"n":"欧美","v":"3"},
{"n":"其他","v":"4"}
]}],

"100109":[{"key":"sort","name":"排序","value":[{"v":"75","n":"最热"},{"v":"23","n":"更新"},{"v":"85","n":"高分"}]},{"key":"area","name":"地区","value":[
{"n":"地区","v":"-1"},
{"n":"国内","v":"1"},
{"n":"海外","v":"2"}
]}],

"100105":[{"key":"sort","name":"排序","value":[{"v":"75","n":"最热"},{"n":"最新","v":"74"},{"v":"85","n":"高分"}]},{"key":"area","name":"地区","value":[
{"n":"地区","v":"-1"},
{"n":"国内","v":"1"},
{"n":"国外","v":"2"}
]}],

"100150":[{"key":"sort","name":"排序","value":[{"v":"75","n":"最热"},{"n":"最新","v":"76"}]}
],

"110755":[{"key":"sort","name":"排序","value":[{"v":"75","n":"最热"},{"n":"最新","v":"76"}]}]
}


return JSON.stringify({ class: classes, filters: filterObj });
}


//主页推荐
async function homeVod() {
let body = {
    "page_params": {"page_id": "100101","page_type": "channel","skip_privacy_types": "0","support_click_scan": "1","new_mark_label_enabled": "1"},
    "page_bypass_params": {
    "params": {"caller_id": "3000010","data_mode": "default","page_id": "100101","page_type": "channel","platform_id": "2","user_mode": "default"},
    "scene": "channel",
    "abtest_bypass_id": "bf94ad015ab2bbaf"
    }
}
let html = await request(`${host}/trpc.vector_layout.page_view.PageService/getPage?video_appid=3000010`, body)
let videos = html.data.CardList[0].children_list.list.cards
  .filter(item => item.params.cid && item.params.title) // 过滤条件
  .map(item => ({
    vod_id: item.params.cid, 
    vod_name: item.params.title, 
    vod_pic: item.params.image_url_vertical, 
    vod_remarks: item.params.stitle_pc, 
    vod_year: ''
  }));

return JSON.stringify({ list: videos });
}

//分类
async function category (tid, pg, filter, extend) {
let body = {
        "page_params": {
        "channel_id": tid,
        "filter_params": `sort=${extend.sort || '75'}&itype=-1&ipay=-1&iarea=${extend.area || ''}&iyear=-1&producer=-1&characteristic=-1`,
        "page_type": "operation",
        "page_id": "channel_list"
        },
        "page_context": {
        "_ctrl_page_index": String(pg-1),
        "_ctrl_showed_module_num": String(pg-1),
        "_ds_cli_6970df954e7a9803_poster_offset": String((pg-1)*12),
        "_ds_cli_6970df954e7a9803_poster_size": "12",
        "_merger_mod_cnt": String(pg-1),
        "page_index": String(pg-1),
        "video_un_page_index": String(pg-1)
        }
}
let html = await request(`${host}/trpc.multi_vector_layout.mvl_controller.MVLPageHTTPService/getMVLPage?&vversion_platform=2`, body)

let videos = html.data.modules.normal.cards.flatMap(card => 
    card.children_list.poster_card.cards
        .filter(item => item.params && (item.params.cid || item.params.title)) // 过滤无效数据
        .map(item => ({
            vod_id: item.params.cid,
            vod_name: item.params.title, 
            vod_pic: item.params.new_pic_vt,
            vod_remarks: item.params.timelong,
            vod_year: item.params.year || ""
        }))
);

return JSON.stringify({ page: pg, pagecount: 99999, limit: videos.length, total: 99999, list: videos });
}

//详情
async function detail (id) {
let body = {
    "page_params":{
        "page_id": "vsite_episode_list",
        "page_type": "detail_operation",
        "page_size": "100",
        "cid": id,
        "page_num": "0"
    }
}
let html = await request(`${host}/trpc.universal_backend_service.page_server_rpc.PageServer/GetPageData?video_appid=3000010&vplatform=2&vversion_name=8.2.96`, body)

//第一页数据
let play_url = [...getlist(html)]

//判断下一页
let p = html.data.module_list_datas[0].module_datas[0].module_params.tabs

if (p) {
let pp = JSON.parse(p)
let Allpage = Math.ceil(pp[pp.length - 1].end_text / 100) - 1
// 循环请求剩余页面
for (let pg = 1; pg <= Allpage; pg++) {
    let bodys = {
        "page_params":{"page_id":"vsite_episode_list","page_type":"detail_operation","page_size":"100","cid":id,"page_num": String(pg)}
    }
    let pageRes = await request(`${host}/trpc.universal_backend_service.page_server_rpc.PageServer/GetPageData?video_appid=3000010&vplatform=2&vversion_name=8.2.96`, bodys);
    play_url.push(...getlist(pageRes));
}
}

let xqbody = {
    "page_params":{"req_from":"web","cid":id,"vid":"","lid":"","page_type":"detail_operation","page_id":"detail_page_introduction"},
    "has_cache":1
}
let res = await request(`${host}/trpc.universal_backend_service.page_server_rpc.PageServer/GetPageData?video_appid=3000010&vversion_name=8.2.98&vversion_platform=2`, xqbody);
let xq = res.data.module_list_datas[0].module_datas[0].item_data_lists.item_datas[0].item_params;

var vod = {
    "type_name": xq.main_genres,
    "vod_year": xq.year,
    "vod_area": xq.area_name,
    "vod_remarks": xq.detail_info.replace(/<[^>]*>| · \d+| /g, ''),
    "vod_content": xq.cover_description,
    "vod_play_from": '正片', 
    "vod_play_url": play_url.join('#')
    }

return JSON.stringify({ list: [vod] })
}


//播放
async function play (flag, id, flags) {

//雨鹿解析
try {
let jx7 = JSON.parse((await req(`http://103.236.55.163:6565/api/index?parsesId=4&appid=10000&videoUrl=${id}`)).content)
if (jx7.code == '200') {
return JSON.stringify({ parse: 0, url: 雨鹿解析(jx7.url), danmaku: `${getProxy(true)}&url=${id}` });
}
} catch (e) {}

//至尊4K 公众号夕颜工作室
try {
let jx1 = JSON.parse((await req(`http://zz2.mftv.top/api/index?parsesId=3&appid=10004&videoUrl=${id}`,{headers:{
    "token": "fdfb1079-e6e2-46a4-ac6d-f88778faf455",
    "User-Agent": "okhttp/4.12.0"
}})).content)
if (jx1.code=='200'){
return JSON.stringify({ parse: 0, url: 畅影解析(jx1.url), danmaku: `${getProxy(true)}&url=${id}` });
}
} catch (e) {}

return JSON.stringify({ parse: 1, url: id, flag: "腾讯", danmaku: `${getProxy(true)}&url=${id}` });
}

//搜索
async function search (wd, quick, pg=1) {
let body = {
    "version": "24060601",
    "clientType": 1,
    "filterValue": "firstTabid=150",
    "retry": 0,
    "query": wd,
    "pagenum": pg-1,
    "pagesize": 14,
    "queryFrom": 4,
    "searchDatakey": "",
    "isneedQc": true,
    "adClientInfo": "",
    "extraInfo": {"isNewMarkLabel": "1"}
}
let html = await request(`${host}/trpc.videosearch.mobile_search.HttpMobileRecall/MbSearchHttp?vplatform=5`, body)

let videos = html.data.normalList.itemList.map(item => ({
        vod_id: item.doc.id,
        vod_name: item.videoInfo.title.replace(/<em>|<\/em>/g,''),
        vod_pic: item.videoInfo.imgUrl, 
        vod_remarks: (item.videoInfo.imgTag.match(/"4":\s*{"info":.*?"text":"([^"]*)"/) || [])[1] || '',
        vod_year: item.videoInfo.year
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