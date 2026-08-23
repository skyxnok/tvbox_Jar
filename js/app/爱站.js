
//https://girigirilove.top  https://bgm.girigirilove.com
let host = 'https://m3u8.girigirilove.com';

async function init(cfg) {
}

//统一请求
async function request(reqUrl, body) {
    const opt = {
        headers: {
            "user-agent": "Dart/3.11 (dart:io)",
            "accept": "application/json",
            "Content-Type": "application/json",
            "cookie": "SITE_TOTAL_ID=67ee3ec6e87dfd18577904b81e8d4a40"
        }
    };
    if (body && typeof body === 'object') {
        opt.body = JSON.stringify(body);
        opt.method = 'POST';
    }
    return JSON.parse((await req(reqUrl, opt)).content);
}

//分类
async function home (filter) {
let html = await request(`${host}/api.php/App2/bannerList`)

//一级
let classes = [{type_id:'2',type_name:'日番'},{type_id:'3',type_name:'美番'},{type_id:'21',type_name:'剧场'}]

//二级
let filterObj = {
  "2":[{"key":"year","name":"年份","value":[{"n":"全部","v":""},{"n":"2026","v":"2026"},{"n":"2025","v":"2025"},{"n":"2024","v":"2024"},{"n":"2023","v":"2023"},{"n":"2022","v":"2022"},{"n":"2021","v":"2021"},{"n":"2020","v":"2020"},{"n":"2019","v":"2019"},{"n":"2018","v":"2018"},{"n":"2017","v":"2017"},{"n":"2016","v":"2016"},{"n":"2015","v":"2015"},{"n":"2014","v":"2014"},{"n":"2013","v":"2013"},{"n":"2012","v":"2012"},{"n":"2011","v":"2011"},{"n":"2010","v":"2010"}]},{"key":"sort","name":"排序","value":[{"n":"最新","v":""},{"n":"最热","v":"hits"},{"n":"评分","v":"score"}]}],
  "3":[{"key":"year","name":"年份","value":[{"n":"全部","v":""},{"n":"2026","v":"2026"},{"n":"2025","v":"2025"},{"n":"2024","v":"2024"},{"n":"2023","v":"2023"},{"n":"2022","v":"2022"},{"n":"2021","v":"2021"},{"n":"2020","v":"2020"},{"n":"2019","v":"2019"},{"n":"2018","v":"2018"},{"n":"2017","v":"2017"},{"n":"2016","v":"2016"},{"n":"2015","v":"2015"},{"n":"2014","v":"2014"},{"n":"2013","v":"2013"},{"n":"2012","v":"2012"},{"n":"2011","v":"2011"},{"n":"2010","v":"2010"}]},{"key":"sort","name":"排序","value":[{"n":"最新","v":""},{"n":"最热","v":"hits"},{"n":"评分","v":"score"}]}],
  "21":[{"key":"year","name":"年份","value":[{"n":"全部","v":""},{"n":"2026","v":"2026"},{"n":"2025","v":"2025"},{"n":"2024","v":"2024"},{"n":"2023","v":"2023"},{"n":"2022","v":"2022"},{"n":"2021","v":"2021"},{"n":"2020","v":"2020"},{"n":"2019","v":"2019"},{"n":"2018","v":"2018"},{"n":"2017","v":"2017"},{"n":"2016","v":"2016"},{"n":"2015","v":"2015"},{"n":"2014","v":"2014"},{"n":"2013","v":"2013"},{"n":"2012","v":"2012"},{"n":"2011","v":"2011"},{"n":"2010","v":"2010"}]},{"key":"sort","name":"排序","value":[{"n":"最新","v":""},{"n":"最热","v":"hits"},{"n":"评分","v":"score"}]}]
}

//推荐
let videos = html.info.filter(item => {
    // 过滤条件：item.title 不能为空且不包含 "tg" 字样
    return item.title && item.title.trim() !== '' && !item.title.toLowerCase().includes('tg');
  }).map(item => ({
    vod_id: item.link.split('/')[2] || '',
    vod_name: item.title,
    vod_pic: item.cover,
//    vod_remarks: '',
//    vod_year: '',
    style: { "type": "rect", "ratio": 1.485 }
}));

return JSON.stringify({ class: classes, filters: filterObj, list: videos });
}


//主页推荐
async function homeVod() {
}

//分类
async function category (tid, pg, filter, extend) {
let offset = (pg-1)*20
let html = await request(`${host}/api.php/Vod/get_list?offset=${offset}&limit=20&type_id=${tid}&vod_year=${extend.year || ''}&orderby=${extend.sort || ''}`)

let videos = html.info.rows.map(item => ({
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
let html = (await request(`${host}/api.php/Vod/get_detail?vod_id=${id}`)).info

var vod = {
    "type_name": html.vod_class,
    "vod_year": html.vod_year,
    "vod_area": html.vod_area,
    "vod_remarks": html.vod_remarks,
    "vod_actor": html.vod_actor,
    "vod_director": html.vod_director,
    "vod_content": html.vod_content.replace(/<.*?>/g, ''),
    "vod_play_from": html.vod_play_from.replace(/chs/,'简体').replace(/cht/,'繁体'), 
    "vod_play_url": html.vod_play_url
    }

return JSON.stringify({ list: [vod] })
}


//播放
async function play (flag, id, flags) {
let dmurl = (await request(`${host}/api.php/Scrolling/getVodOutScrolling`, {"play_url":id})).info

return JSON.stringify({ parse: 0,url: id, danmaku: dmurl })
}

//搜索
async function search (wd, quick, pg=1) {
let offset = (pg-1)*20
let html = await request(`${host}/api.php/Vod/get_list?vod_name=${wd}&offset=${offset}&limit=20`)

let videos = html.info.rows.map(item => ({
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
