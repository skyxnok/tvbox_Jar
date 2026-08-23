//import "assets://js/lib/crypto-js.js";

//https://dubokutv.supertws.com/#download
let host = 'https://api.dbokutv.com';

//请求头
let hh = {
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64)",
        "Referer": "https://www.duboku.tv/"
}

//生成sign
function getsign(rawUrl) {
  // 1. 生成随机数和时间戳
  const timestamp = Math.floor(Date.now() / 1000);
  const randomNum = Math.floor(Math.random() * 800000001);
  const valueA = randomNum + 100000000;
  const valueB = 900000000 - randomNum;
  
  // 2. 交错合并字符串
  const interleave = (a, b) => {
    let result = '';
    const minLen = Math.min(a.length, b.length);
    for (let i = 0; i < minLen; i++) {
      result += a[i] + b[i];
    }
    return result + a.slice(minLen) + b.slice(minLen);
  };
  
  const interleaved = interleave(`${valueA}${valueB}`, `${timestamp}`);
  
  // 3. Base64编码并替换等号
  const ssid = btoa(interleaved).replace(/=/g, '.')
  
  // 4. 生成随机字符串
  const randomStr = (len) => {
    const chars = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
    return Array.from({length: len}, () => 
      chars[Math.floor(Math.random() * chars.length)]
    ).join('');
  };
  
  // 5. 拼接最终URL 随机60位 38位字符串
  return `${rawUrl}?sign=${randomStr(60)}&token=${randomStr(38)}&ssid=${ssid}`;
}

//解密
function decode(data) {
//每10个字符分割，分割后反转字符串，然后拼接起来base64解码
let str = data.replace(/^['"]+|['"]+$/g, '').replace(/\./g, '=')
let res = str.match(/.{1,10}/g).map(s => [...s].reverse().join('')).join('');
return atob(res)
}


async function init(cfg) {
}

//分类
async function home (filter) {

let classes = [{"type_id":1,"type_name":"电影"},{"type_id":2,"type_name":"剧集"},{"type_id":3,"type_name":"综艺"},{"type_id":4,"type_name":"动漫"},{"type_id":20,"type_name":"港剧"}]
let filterObj = {"1":[{"key":"class","name":"类型","value":[{"n":"剧情","v":""},{"n":"喜剧","v":"喜剧"},{"n":"爱情","v":"爱情"},{"n":"恐怖","v":"恐怖"},{"n":"动作","v":"动作"},{"n":"科幻","v":"科幻"},{"n":"剧情","v":"剧情"},{"n":"警匪","v":"警匪"},{"n":"战争","v":"战争"},{"n":"犯罪","v":"犯罪"},{"n":"动画","v":"动画"},{"n":"奇幻","v":"奇幻"},{"n":"武侠","v":"武侠"},{"n":"冒险","v":"冒险"},{"n":"悬疑","v":"悬疑"},{"n":"惊悚","v":"惊悚"},{"n":"古装","v":"古装"}]},{"key":"area","name":"地区","value":[{"n":"地区","v":""},{"n":"大陆","v":"大陆"},{"n":"香港","v":"香港"},{"n":"台湾","v":"台湾"},{"n":"韩国","v":"韩国"},{"n":"英国","v":"英国"},{"n":"法国","v":"法国"},{"n":"加拿大","v":"加拿大"},{"n":"澳大利亚","v":"澳大利亚"}]},{"key":"lang","name":"语言","value":[{"n":"语言","v":""},{"n":"国语","v":"国语"},{"n":"粤语","v":"粤语"},{"n":"韩语","v":"韩语"},{"n":"英语","v":"英语"},{"n":"法语","v":"法语"}]},{"key":"year","name":"年份","value":[{"n":"年份","v":""},{"n":"2026","v":"2026"},{"n":"2025","v":"2025"},{"n":"2024","v":"2024"},{"n":"2023","v":"2023"},{"n":"2022","v":"2022"},{"n":"2020","v":"2020"},{"n":"2019","v":"2019"}]},{"key":"sort","name":"排序","value":[{"n":"时间","v":""},{"n":"人气","v":"人气"},{"n":"评分","v":"评分"}]}],"2":[{"key":"class","name":"类型","value":[{"n":"剧情","v":""},{"n":"悬疑","v":"悬疑"},{"n":"武侠","v":"武侠"},{"n":"科幻","v":"科幻"},{"n":"都市","v":"都市"},{"n":"爱情","v":"爱情"},{"n":"古装","v":"古装"},{"n":"战争","v":"战争"},{"n":"青春","v":"青春"},{"n":"偶像","v":"偶像"},{"n":"喜剧","v":"喜剧"},{"n":"家庭","v":"家庭"},{"n":"奇幻","v":"奇幻"},{"n":"剧情","v":"剧情"},{"n":"乡村","v":"乡村"},{"n":"年代","v":"年代"},{"n":"警匪","v":"警匪"},{"n":"谍战","v":"谍战"},{"n":"历险","v":"历险"},{"n":"罪案","v":"罪案"},{"n":"宫廷","v":"宫廷"},{"n":"经典","v":"经典"},{"n":"动作","v":"动作"},{"n":"惊悚","v":"惊悚"},{"n":"历史","v":"历史"},{"n":"穿越","v":"穿越"}]},{"key":"area","name":"地区","value":[{"n":"地区","v":""},{"n":"大陆","v":"大陆"},{"n":"香港","v":"香港"},{"n":"台湾","v":"台湾"},{"n":"韩国","v":"韩国"},{"n":"日本","v":"日本"},{"n":"新加坡","v":"新加坡"},{"n":"泰国","v":"泰国"}]},{"key":"lang","name":"语言","value":[{"n":"语言","v":""},{"n":"国语","v":"国语"},{"n":"粤语","v":"粤语"},{"n":"韩语","v":"韩语"},{"n":"泰语","v":"泰语"},{"n":"日语","v":"日语"}]},{"key":"year","name":"年份","value":[{"n":"年份","v":""},{"n":"2026","v":"2026"},{"n":"2025","v":"2025"},{"n":"2024","v":"2024"},{"n":"2023","v":"2023"},{"n":"2022","v":"2022"},{"n":"2020","v":"2020"},{"n":"2019","v":"2019"}]},{"key":"sort","name":"排序","value":[{"n":"时间","v":""},{"n":"人气","v":"人气"},{"n":"评分","v":"评分"}]}],"3":[{"key":"class","name":"类型","value":[{"n":"剧情","v":""},{"n":"真人秀","v":"真人秀"},{"n":"选秀","v":"选秀"},{"n":"竞演","v":"竞演"},{"n":"情感","v":"情感"},{"n":"旅游","v":"旅游"},{"n":"音乐","v":"音乐"},{"n":"美食","v":"美食"},{"n":"纪实","v":"纪实"},{"n":"生活","v":"生活"},{"n":"游戏互动","v":"游戏互动"},{"n":"竞技","v":"竞技"},{"n":"搞笑","v":"搞笑"},{"n":"脱口秀","v":"脱口秀"}]},{"key":"area","name":"地区","value":[{"n":"地区","v":""},{"n":"大陆","v":"大陆"},{"n":"韩国","v":"韩国"}]},{"key":"lang","name":"语言","value":[{"n":"语言","v":""},{"n":"国语","v":"国语"},{"n":"韩语","v":"韩语"}]},{"key":"year","name":"年份","value":[{"n":"年份","v":""},{"n":"2026","v":"2026"},{"n":"2025","v":"2025"},{"n":"2024","v":"2024"},{"n":"2023","v":"2023"},{"n":"2022","v":"2022"},{"n":"2020","v":"2020"},{"n":"2019","v":"2019"}]},{"key":"sort","name":"排序","value":[{"n":"时间","v":""},{"n":"人气","v":"人气"},{"n":"评分","v":"评分"}]}],"4":[{"key":"class","name":"类型","value":[{"n":"剧情","v":""},{"n":"武侠","v":"武侠"},{"n":"科幻","v":"科幻"},{"n":"热血","v":"热血"},{"n":"推理","v":"推理"},{"n":"爆笑","v":"爆笑"},{"n":"冒险","v":"冒险"},{"n":"校园","v":"校园"},{"n":"动作","v":"动作"},{"n":"机战","v":"机战"},{"n":"竞技","v":"竞技"},{"n":"少女","v":"少女"},{"n":"格斗","v":"格斗"},{"n":"恋爱","v":"恋爱"},{"n":"魔幻","v":"魔幻"}]},{"key":"area","name":"地区","value":[{"n":"地区","v":""},{"n":"大陆","v":"大陆"},{"n":"日本","v":"日本"},{"n":"法国","v":"法国"},{"n":"美国","v":"美国"}]},{"key":"lang","name":"语言","value":[{"n":"语言","v":""},{"n":"国语","v":"国语"},{"n":"日语","v":"日语"},{"n":"英语","v":"英语"}]},{"key":"year","name":"年份","value":[{"n":"年份","v":""},{"n":"2026","v":"2026"},{"n":"2025","v":"2025"},{"n":"2024","v":"2024"},{"n":"2023","v":"2023"},{"n":"2022","v":"2022"},{"n":"2020","v":"2020"},{"n":"2019","v":"2019"}]},{"key":"sort","name":"排序","value":[{"n":"时间","v":""},{"n":"人气","v":"人气"},{"n":"评分","v":"评分"}]}],"20":[{"key":"class","name":"类型","value":[{"n":"剧情","v":""},{"n":"悬疑","v":"悬疑"},{"n":"武侠","v":"武侠"},{"n":"科幻","v":"科幻"},{"n":"都市","v":"都市"},{"n":"爱情","v":"爱情"},{"n":"古装","v":"古装"},{"n":"战争","v":"战争"},{"n":"青春","v":"青春"},{"n":"偶像","v":"偶像"},{"n":"喜剧","v":"喜剧"},{"n":"家庭","v":"家庭"},{"n":"奇幻","v":"奇幻"},{"n":"剧情","v":"剧情"},{"n":"乡村","v":"乡村"},{"n":"年代","v":"年代"},{"n":"警匪","v":"警匪"},{"n":"谍战","v":"谍战"},{"n":"历险","v":"历险"},{"n":"罪案","v":"罪案"},{"n":"宫廷","v":"宫廷"},{"n":"经典","v":"经典"},{"n":"动作","v":"动作"},{"n":"惊悚","v":"惊悚"},{"n":"历史","v":"历史"},{"n":"穿越","v":"穿越"}]},{"key":"area","name":"地区","value":[{"n":"地区","v":""},{"n":"大陆","v":"大陆"},{"n":"香港","v":"香港"},{"n":"台湾","v":"台湾"},{"n":"韩国","v":"韩国"},{"n":"日本","v":"日本"},{"n":"新加坡","v":"新加坡"},{"n":"泰国","v":"泰国"}]},{"key":"lang","name":"语言","value":[{"n":"语言","v":""},{"n":"国语","v":"国语"},{"n":"粤语","v":"粤语"},{"n":"韩语","v":"韩语"},{"n":"泰语","v":"泰语"},{"n":"日语","v":"日语"}]},{"key":"year","name":"年份","value":[{"n":"年份","v":""},{"n":"2026","v":"2026"},{"n":"2025","v":"2025"},{"n":"2024","v":"2024"},{"n":"2023","v":"2023"},{"n":"2022","v":"2022"},{"n":"2020","v":"2020"},{"n":"2019","v":"2019"}]},{"key":"sort","name":"排序","value":[{"n":"时间","v":""},{"n":"人气","v":"人气"},{"n":"评分","v":"评分"}]}]}

return JSON.stringify({ class: classes, filters: filterObj });
}


//主页推荐
async function homeVod() {
let html = JSON.parse((await req(getsign(`${host}/home`), {headers: hh })).content)

let videos = html[0].VodList.map(item => ({
  vod_id: decode(item.DId),
  vod_name: item.Name,
  vod_pic: decode(item.TnId),
  vod_remarks: `评分:${item.Rating} | ${item.Tag}`,
//  style: {"type": "rect", "ratio": 1.485 }
}));

return JSON.stringify({ list: videos })
}

//分类
async function category (tid, pg, filter, extend) {
let html = JSON.parse((await req(getsign(`${host}/vodshow/${tid}-${extend.area || ''}-${extend.sort || ''}-${extend.class || ''}-${extend.lang || ''}----${pg}---${extend.year || ''}`), {headers: hh })).content)

let videos = html.VodList.map(item => ({
  vod_id: decode(item.DId),
  vod_name: item.Name,
  vod_pic: decode(item.TnId),
  vod_remarks: `评分:${item.Rating} | ${item.Tag}`,
//  style: {"type": "rect", "ratio": 1.485 }
}));

return JSON.stringify({ page: pg, pagecount: 99999, limit: videos.length, total: 99999, list: videos });
}

//详情
async function detail (id) {
let html = JSON.parse((await req(getsign(`${host}${id}`), {headers: hh })).content)

let play_url = html.Playlist.map(item => { return `${item.EpisodeName}$${decode(item.VId)}` }).join('#');

var vod = {
    "type_name": html.Genre,
    "vod_year": html.ReleaseYear,
    "vod_area": html.Region,
    "vod_actor": "",
    "vod_director": "",
    "vod_content": html.Description,
    "vod_play_from": '独播库', 
    "vod_play_url": play_url
    }

return JSON.stringify({ list: [vod] })
}


//播放
async function play (flag, id, flags) {

let url = JSON.parse((await req(getsign(`${host}${id}`), {headers: hh })).content).HId
return JSON.stringify ({parse: 0,url: decode(url)})

}

//搜索
async function search (wd, quick, pg=1) {
let url = getsign(`${host}/vodsearch`)
let html = JSON.parse((await req(`${url}&wd=${wd}`, {headers: hh })).content)

let videos = html.map(item => ({
  vod_id: decode(item.DId),
  vod_name: item.Name,
  vod_pic: decode(item.TnId),
  vod_remarks: `评分:${item.Rating} | ${item.Tag}`,
//  style: {"type": "rect", "ratio": 1.485 }
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