//import "assets://js/lib/crypto-js.js";

//let host = 'https://www.vv3nwjk.com';
let host;

async function init(cfg) {
//host = (await req('https://www.jpyy.com', { redirect: 0 })).headers.location
host = 'https://www.hskjjglo.com'
}
async function request(url) {
let t = Date.now();
let str = url.split('?')[1] ?? '';
let signstr = str ? `${str}&key=cb808529bae6b6be45ecfab29a4889bc&t=${t}` : `key=cb808529bae6b6be45ecfab29a4889bc&t=${t}`;
let sign = sha1X(md5X(signstr));
let res = (await req(url, {
  headers: { 
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.6261.95 Safari/537.36',
    'Referer': host,
    "t": t,
    "sign": sign
  }
})).content;
return JSON.parse(res);
}

//分类
async function home (filter) {

//一级
let classes = [{type_id:'1',type_name:'电影'},{type_id:'2',type_name:'电视'},{type_id:'3',type_name:'综艺'},{type_id:'4',type_name:'动漫'},{type_id:'88',type_name:'短剧'}];

//二级
let filterObj = {"1":[{"key":"type","name":"类型","value":[{"n":"喜剧","v":"22"},{"n":"动作","v":"23"},{"n":"科幻","v":"30"},{"n":"爱情","v":"26"},{"n":"悬疑","v":"27"},{"n":"奇幻","v":"87"},{"n":"剧情","v":"37"},{"n":"恐怖","v":"36"},{"n":"犯罪","v":"35"},{"n":"动画","v":"33"},{"n":"惊悚","v":"34"},{"n":"战争","v":"25"},{"n":"冒险","v":"31"},{"n":"灾难","v":"81"},{"n":"伦理","v":"83"},{"n":"其他","v":"43"}]},{"key":"class","name":"剧情","value":[{"n":"爱情","v":"爱情"},{"n":"动作","v":"动作"},{"n":"喜剧","v":"喜剧"},{"n":"战争","v":"战争"},{"n":"科幻","v":"科幻"},{"n":"剧情","v":"剧情"},{"n":"武侠","v":"武侠"},{"n":"冒险","v":"冒险"},{"n":"枪战","v":"枪战"},{"n":"恐怖","v":"恐怖"},{"n":"微电影","v":"微电影"},{"n":"其它","v":"其它"}]},{"key":"area","name":"地区","value":[{"n":"中国大陆","v":"中国大陆"},{"n":"中国香港","v":"中国香港"},{"n":"中国台湾","v":"中国台湾"},{"n":"美国","v":"美国"},{"n":"日本","v":"日本"},{"n":"韩国","v":"韩国"},{"n":"印度","v":"印度"},{"n":"泰国","v":"泰国"},{"n":"英国","v":"英国"},{"n":"法国","v":"法国"},{"n":"其他","v":"其他"}]},{"key":"year","name":"年份","value":[{"n":"2025","v":"2025"},{"n":"2024","v":"2024"},{"n":"2023","v":"2023"},{"n":"2022","v":"2022"},{"n":"2021","v":"2021"},{"n":"2020","v":"2020"},{"n":"2019","v":"2019"},{"n":"2018","v":"2018"},{"n":"2017","v":"2017"},{"n":"2016","v":"2016"},{"n":"2015","v":"2015"},{"n":"2014","v":"2014"},{"n":"2013","v":"2013"},{"n":"2012","v":"2012"},{"n":"2011","v":"2011"},{"n":"2010","v":"2010"},{"n":"2009~2000","v":"2009~2000"}]},{"key":"lang","name":"语言","value":[{"n":"国语","v":"国语"},{"n":"英语","v":"英语"},{"n":"粤语","v":"粤语"},{"n":"韩语","v":"韩语"},{"n":"日语","v":"日语"},{"n":"其他","v":"其他"}]},{"key":"sort","name":"排序","value":[{"n":"更新","v":"2"},{"n":"人气","v":"3"},{"n":"评分","v":"4"}]}],"2":[{"key":"type","name":"类型","value":[{"n":"国产剧","v":"14"},{"n":"欧美剧","v":"15"},{"n":"港台剧","v":"16"},{"n":"日韩剧","v":"62"},{"n":"其他剧","v":"68"}]},{"key":"class","name":"剧情","value":[{"n":"古装","v":"古装"},{"n":"战争","v":"战争"},{"n":"喜剧","v":"喜剧"},{"n":"家庭","v":"家庭"},{"n":"犯罪","v":"犯罪"},{"n":"动作","v":"动作"},{"n":"奇幻","v":"奇幻"},{"n":"剧情","v":"剧情"},{"n":"历史","v":"历史"},{"n":"短片","v":"短片"},{"n":"其它","v":"其它"}]},{"key":"area","name":"地区","value":[{"n":"中国大陆","v":"中国大陆"},{"n":"中国香港","v":"中国香港"},{"n":"中国台湾","v":"中国台湾"},{"n":"日本","v":"日本"},{"n":"韩国","v":"韩国"},{"n":"美国","v":"美国"},{"n":"泰国","v":"泰国"},{"n":"其他","v":"其他"}]},{"key":"year","name":"年份","value":[{"n":"2025","v":"2025"},{"n":"2024","v":"2024"},{"n":"2023","v":"2023"},{"n":"2022","v":"2022"},{"n":"2021","v":"2021"},{"n":"2020","v":"2020"},{"n":"2019","v":"2019"},{"n":"2018","v":"2018"},{"n":"2017","v":"2017"},{"n":"2016","v":"2016"},{"n":"2015","v":"2015"},{"n":"2014","v":"2014"},{"n":"2013","v":"2013"},{"n":"2012","v":"2012"},{"n":"2011","v":"2011"},{"n":"2010","v":"2010"}]},{"key":"lang","name":"语言","value":[{"n":"普通话","v":"普通话"},{"n":"英语","v":"英语"},{"n":"粤语","v":"粤语"},{"n":"韩语","v":"韩语"},{"n":"日语","v":"日语"},{"n":"泰语","v":"泰语"},{"n":"其他","v":"其他"}]},{"key":"sort","name":"排序","value":[{"n":"更新","v":"2"},{"n":"人气","v":"3"},{"n":"评分","v":"4"}]}],"3":[{"key":"type","name":"类型","value":[{"n":"国产综艺","v":"69"},{"n":"港台综艺","v":"70"},{"n":"日韩综艺","v":"72"},{"n":"欧美综艺","v":"73"}]},{"key":"class","name":"剧情","value":[{"n":"真人秀","v":"真人秀"},{"n":"音乐","v":"音乐"},{"n":"脱口秀","v":"脱口秀"}]},{"key":"area","name":"地区","value":[{"n":"中国大陆","v":"中国大陆"},{"n":"中国香港","v":"中国香港"},{"n":"中国台湾","v":"中国台湾"},{"n":"日本","v":"日本"},{"n":"韩国","v":"韩国"},{"n":"美国","v":"美国"},{"n":"其他","v":"其他"}]},{"key":"year","name":"年份","value":[{"n":"2025","v":"2025"},{"n":"2024","v":"2024"},{"n":"2023","v":"2023"},{"n":"2022","v":"2022"},{"n":"2021","v":"2021"},{"n":"2020","v":"2020"}]},{"key":"lang","name":"语言","value":[{"n":"国语","v":"国语"},{"n":"英语","v":"英语"},{"n":"粤语","v":"粤语"},{"n":"韩语","v":"韩语"},{"n":"日语","v":"日语"},{"n":"其他","v":"其他"}]},{"key":"sort","name":"排序","value":[{"n":"更新","v":"2"},{"n":"人气","v":"3"},{"n":"评分","v":"4"}]}],"4":[{"key":"type","name":"类型","value":[{"n":"国产动漫","v":"75"},{"n":"日韩动漫","v":"76"},{"n":"欧美动漫","v":"77"}]},{"key":"class","name":"剧情","value":[{"n":"喜剧","v":"喜剧"},{"n":"科幻","v":"科幻"},{"n":"热血","v":"热血"},{"n":"冒险","v":"冒险"},{"n":"动作","v":"动作"},{"n":"运动","v":"运动"},{"n":"战争","v":"战争"},{"n":"少女","v":"少女"},{"n":"动画","v":"动画"}]},{"key":"area","name":"地区","value":[{"n":"中国大陆","v":"中国大陆"},{"n":"日本","v":"日本"},{"n":"美国","v":"美国"},{"n":"其他","v":"其他"}]},{"key":"year","name":"年份","value":[{"n":"2025","v":"2025"},{"n":"2024","v":"2024"},{"n":"2023","v":"2023"},{"n":"2022","v":"2022"},{"n":"2021","v":"2021"},{"n":"2020","v":"2020"},{"n":"2019","v":"2019"},{"n":"2018","v":"2018"},{"n":"2017","v":"2017"},{"n":"2016","v":"2016"},{"n":"2015","v":"2015"},{"n":"2014","v":"2014"},{"n":"2013","v":"2013"},{"n":"2012","v":"2012"},{"n":"2011","v":"2011"},{"n":"2010","v":"2010"}]},{"key":"lang","name":"语言","value":[{"n":"国语","v":"国语"},{"n":"英语","v":"英语"},{"n":"日语","v":"日语"},{"n":"其他","v":"其他"}]},{"key":"sort","name":"排序","value":[{"n":"更新","v":"2"},{"n":"人气","v":"3"},{"n":"评分","v":"4"}]}]}

return JSON.stringify({ class: classes, filters: filterObj });
}


//主页推荐
async function homeVod() {
let html = (await request(`${host}/api/mw-movie/anonymous/home/hotSearch?`)).data;

let videos = html.map(item => ({
    vod_id: item.vodId,
    vod_name: item.vodName,
    vod_pic: item.vodPic,
    vod_remarks: item.vodRemarks,
    vod_year: ""
}));

return JSON.stringify({ list: videos });
}

//分类
async function category (tid, pg, filter, extend) {
let html = (await request(`${host}/api/mw-movie/anonymous/video/list?area=${extend.area || ''}&lang=${extend.lang || ''}&pageNum=${pg}&pageSize=30&sort=${extend.sort || '3'}&sortBy=1&type=${extend.type || ''}&type1=${tid}&v_class=${extend.class || ''}&year=${extend.year || ''}`)).data.list

let videos = html.map(item => ({
    vod_id: item.vodId,
    vod_name: item.vodName,
    vod_pic: item.vodPic,
    vod_remarks: item.vodRemarks,
    vod_year: ""
}));

return JSON.stringify({ page: pg, pagecount: 99999, limit: videos.length, total: 99999, list: videos });
}

//详情
async function detail (id) {
let html = (await request(`${host}/api/mw-movie/anonymous/video/detail?id=${id}`)).data

let play_from = html.vodVersion;
let play_url =  html.episodeList.map(item => {
    return `${item.name}$${id}@@${item.nid}`;
}).join('#');

var vod = {
    "type_name": html.typeName,
    "vod_year": html.vodYear,
    "vod_area": html.vodArea,
    "vod_actor": html.vodActor,
    "vod_director": html.vodDirector,
    "vod_remarks": html.vodRemarks,
    "vod_content": html.vodContent,
    "vod_play_from": play_from, 
    "vod_play_url": play_url
    }

return JSON.stringify({ list: [vod] })
}


//播放
async function play (flag, id, flags) {
let [ids, nid] = id.split("@@");
let html = await request(`${host}/api/mw-movie/anonymous/v2/video/episode/url?clientType=1&id=${ids}&nid=${nid}`)

let url = html.data.list.flatMap(i=>[i.resolutionName, i.url]);

return JSON.stringify({ parse: 0, url: url });
}

//搜索
async function search (wd, quick, pg=1) {
let html = (await request(`${host}/api/mw-movie/anonymous/video/searchByWord?keyword=${wd}&pageNum=${pg||1}&pageSize=12&sourceCode=1`)).data.result.list
let videos = html.map(item => ({
    vod_id: item.vodId,
    vod_name: item.vodName,
    vod_pic: item.vodPic,
    vod_remarks: item.vodRemarks,
    vod_year: item.vodYear
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