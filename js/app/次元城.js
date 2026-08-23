

//https://www.cycity.pro 
//let host = 'https://mapi.babel.gold';
let host;
let token;

async function init(cfg) {
host = JSON.parse((await req(`https://doh.pub/dns-query?name=newapp.cycapp.org&type=txt`)).content).Answer[0].data.replace(/\"/g,'')
token = JSON.parse((await req(`${host}/auth/login`, {
    body: JSON.stringify({"username": "2948853431","password": "zz77226"}),
    headers: {"x-app-name": "cyc_android","accept": "application/json","user-agent": "ktor-client", "content-type": "application/json"},
    method: 'POST'
  } )).content)?.data?.token
hh['authorization'] = token
}

let hh = {
    "x-app-name": "cyc_android",
    "accept": "application/json",
    "user-agent": "ktor-client"
}

//代理
async function proxy(params) {
let html = JSON.parse((await req(`${host}/sections/${params.id}/danmaku`,{headers: hh}) ).content)  
let xml = getXML(html);
return [200, "application/xml", xml];
}

//转换XML弹幕
function getXML(json) {
    const xmlParts = ['<i>\n'];
    const danmukuList = json?.data?.items || [];
    const len = danmukuList.length;

    for (let i = 0; i < len; i++) {
        const danmakuObj = danmukuList[i];
        const time = danmakuObj.time_point; // 弹幕时间 秒，浮点数
        const mode = 1; 
        const content = danmakuObj.content || '';
        const fontSize = 24;
        const color = danmakuObj.color ?? 16777215;

        let cleanContent = content.replace(/.+公众号.+|\&|\<|\>|.+微信.+|.+yunyun.+|.+com.+|.+www.+|戌入.+|戍‍人.+|.+rj999.+/g, '');
        
        cleanContent = cleanContent
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&apos;');

        xmlParts.push(`    <d p="${time},${mode},${fontSize},${color}">${cleanContent}</d>\n`);
    }
    xmlParts.push('</i>');
    return xmlParts.join('');
}

//统一请求
async function request(reqUrl, body) {
    let opt = { headers: hh };
    let res = (await req(reqUrl, opt)).content
    return JSON.parse(res);
}

//构建query字符串
function buildQuery(obj) {
    const arr = [];
    for(const k in obj) {
        const v = obj[k];
        // 过滤空：null undefined 空字符串直接跳过；0/false保留
        if(v == null || v === '') continue;
        arr.push(`${k}=${v}`);
    }
    return arr.join('&');
}


//分类
async function home (filter) {
let html = await request(`${host}/app/adverts?position=banner`)

//一级
let classes = [{type_id:'1',type_name:'TV番'},{type_id:'2',type_name:'剧场版'}]
//二级
let filterObj = {
  "1":[{"key":"year","name":"年份","value":[{"n":"全部","v":""},{"n":"2026","v":"2026"},{"n":"2025","v":"2025"},{"n":"2024","v":"2024"},{"n":"2023","v":"2023"},{"n":"2022","v":"2022"},{"n":"2021","v":"2021"},{"n":"2020","v":"2020"},{"n":"2019","v":"2019"},{"n":"2018","v":"2018"},{"n":"2017","v":"2017"},{"n":"2016","v":"2016"},{"n":"2015","v":"2015"},{"n":"2014","v":"2014"},{"n":"2013","v":"2013"},{"n":"2012","v":"2012"},{"n":"2011","v":"2011"},{"n":"2010","v":"2010"}]},{"key":"sort","name":"排序","value":[{"n":"最新","v":"update_time"},{"n":"最热","v":"hits"},{"n":"评分","v":"score"}]}],
  "2":[{"key":"year","name":"年份","value":[{"n":"全部","v":""},{"n":"2026","v":"2026"},{"n":"2025","v":"2025"},{"n":"2024","v":"2024"},{"n":"2023","v":"2023"},{"n":"2022","v":"2022"},{"n":"2021","v":"2021"},{"n":"2020","v":"2020"},{"n":"2019","v":"2019"},{"n":"2018","v":"2018"},{"n":"2017","v":"2017"},{"n":"2016","v":"2016"},{"n":"2015","v":"2015"},{"n":"2014","v":"2014"},{"n":"2013","v":"2013"},{"n":"2012","v":"2012"},{"n":"2011","v":"2011"},{"n":"2010","v":"2010"}]},{"key":"sort","name":"排序","value":[{"n":"最新","v":"update_time"},{"n":"最热","v":"hits"},{"n":"评分","v":"score"}]}]
}

//推荐
let videos = html.data.list.map(item => ({
    vod_id: item.action_value,
    vod_name: item.name,
    vod_pic: item.content,
    style: {"type": "rect", "ratio": 1.485 }
}));

return JSON.stringify({ class: classes, filters: filterObj, list: videos });
}


//主页推荐
async function homeVod() {
}

//分类
async function category (tid, pg, filter, extend) {
const params = {
    zone_id: tid,
    page: pg,
    page_size: 20,
    order_by: extend.sort || 'update_time',
    year: extend.year
};

let html = await request(`${host}/videos?${buildQuery(params)}`);

//let html = await request(`${host}/videos?zone_id=${tid}&page=${pg}&page_size=20&order_by=${extend.sort || 'update_time'}`)

let videos = html.data.list.map(item => ({
    vod_id: item.video_id,
    vod_name: item.title,
    vod_pic: item.cover_url,
    vod_remarks: item.remarks,
    vod_year: item.year
}));

return JSON.stringify({ page: pg, pagecount: 99999, limit: videos.length, total: 99999, list: videos });
}

//详情
async function detail (id) {
let html = (await request(`${host}/videos/${id}`)).data
let play_from = html.play_from.map(item => item.title ).join('$$$')
let play_url = [];
for (let i = 0; i < html.play_from.length; i++) {
    const item = html.play_from[i];
    let code = item.code;
    let total = item.count;
    let totalPage = Math.ceil(total / 100);

    const pageTasks = [];
    for (let page = 1; page <= totalPage; page++) {
        const url = `${host}/videos/${id}/sections?player_code=${code}&page=${page}&page_size=100`;
        const opt = {
            headers: hh,
            method: 'GET'
        };
        const task = http(url, opt).then(r => {
            const resp = JSON.parse(r.content);
            return resp.data.list.map(sub => `${sub.title}$${sub.id}`);
        });
        pageTasks.push(task);
    }

    const pageResultList = await Promise.all(pageTasks);

    let allEpList = [];
    for (const pageItems of pageResultList) {
        allEpList.push(...pageItems);
    }
    play_url.push(allEpList.join('#'));
}

var vod = {
    "vod_name": html.title,
    "type_name": html.state,
    "vod_year": html.year,
    "vod_area": html.area,
    "vod_remarks": html.vod_remarks,
    "vod_actor": html.actor?.join(' / ') || '',
    "vod_director": html.director?.join(' / ') || '',
    "vod_content": html.description,
    "vod_play_from": play_from, 
    "vod_play_url": play_url.join('$$$')
    }

return JSON.stringify({ list: [vod] })
}


//播放
async function play (flag, id, flags) {
let res = await request(`${host}/v2/sections/${id}/play-url`)

return JSON.stringify({ parse: 0,url: res.data.url, danmaku: `${getProxy(true)}&id=${id}` })
}

//搜索
async function search (wd, quick, pg=1) {

let html = await request(`${host}/videos/search?q=${wd}&page=${pg}&page_size=20`)

let videos = html.data.list.map(item => ({
    vod_id: item.video_id,
    vod_name: item.title,
    vod_pic: item.cover_url,
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
      search: search,
      proxy: proxy
  };
}