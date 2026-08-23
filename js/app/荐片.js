
//https://jianpian16.com/#/home

let host, imghost;

async function init(cfg) {
host = cfg.ext?.host || "https://japi.zxfmj.com"
//imghost = `https://${JSON.parse((await req(`${host}/api/v2/settings/resourceDomainConfig`)).content).data.imgDomain.split(',')[0]}`;
let res = `https://${JSON.parse((await req(`${host}/api/v2/settings/packageDomainConfig`)).content).data.imgDomain}`;
imghost = res.includes(',') ? res.split(',')[0] : res
}

async function request(reqUrl) {
  const res = await req(reqUrl, {
    headers: {
      'User-Agent': 'Mozilla/5.0 (Linux; Android 9; V2196A Build/PQ3A.190705.08211809; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/91.0.4472.114 Mobile Safari/537.36;webank/h5face;webank/1.0;netType:NETWORK_WIFI;appVersion:416;packageName:com.jp3.xg3',
      'Referer': host
    }
  });
  return res.content;
}

//分类数据
async function home (filter) {
  let classes = [{type_id:'1',type_name:'电影',},{type_id:'2',type_name:'电视剧',},{type_id:'3',type_name:'动漫',},{type_id:'4',type_name:'综艺',}];

  let filterObj = {"1":[{"key":"cateId","name":"分类","value":[{"v":"1","n":"剧情"},{"v":"2","n":"爱情"},{"v":"3","n":"动画"},{"v":"4","n":"喜剧"},{"v":"5","n":"战争"},{"v":"6","n":"歌舞"},{"v":"7","n":"古装"},{"v":"8","n":"奇幻"},{"v":"9","n":"冒险"},{"v":"10","n":"动作"},{"v":"11","n":"科幻"},{"v":"12","n":"悬疑"},{"v":"13","n":"犯罪"},{"v":"14","n":"家庭"},{"v":"15","n":"传记"},{"v":"16","n":"运动"},{"v":"18","n":"惊悚"},{"v":"20","n":"短片"},{"v":"21","n":"历史"},{"v":"22","n":"音乐"},{"v":"23","n":"西部"},{"v":"24","n":"武侠"},{"v":"25","n":"恐怖"}]},{"key":"area","name":"地區","value":[{"v":"1","n":"国产"},{"v":"3","n":"中国香港"},{"v":"6","n":"中国台湾"},{"v":"5","n":"美国"},{"v":"18","n":"韩国"},{"v":"2","n":"日本"}]},{"key":"year","name":"年代","value":[{"v":"162","n":"2026"},{"v":"107","n":"2025"},{"v":"119","n":"2024"},{"v":"153","n":"2023"},{"v":"101","n":"2022"},{"v":"118","n":"2021"},{"v":"16","n":"2020"},{"v":"7","n":"2019"},{"v":"2","n":"2018"},{"v":"3","n":"2017"},{"v":"22","n":"2016"},{"v":"2015","n":"2015以前"}]},{"key":"sort","name":"排序","value":[{"v":"update","n":"最新"},{"v":"hot","n":"最热"},{"v":"rating","n":"评分"}]}],"2":[{"key":"cateId","name":"分类","value":[{"v":"1","n":"剧情"},{"v":"2","n":"爱情"},{"v":"3","n":"动画"},{"v":"4","n":"喜剧"},{"v":"5","n":"战争"},{"v":"6","n":"歌舞"},{"v":"7","n":"古装"},{"v":"8","n":"奇幻"},{"v":"9","n":"冒险"},{"v":"10","n":"动作"},{"v":"11","n":"科幻"},{"v":"12","n":"悬疑"},{"v":"13","n":"犯罪"},{"v":"14","n":"家庭"},{"v":"15","n":"传记"},{"v":"16","n":"运动"},{"v":"18","n":"惊悚"},{"v":"20","n":"短片"},{"v":"21","n":"历史"},{"v":"22","n":"音乐"},{"v":"23","n":"西部"},{"v":"24","n":"武侠"},{"v":"25","n":"恐怖"}]},{"key":"area","name":"地區","value":[{"v":"1","n":"国产"},{"v":"3","n":"中国香港"},{"v":"6","n":"中国台湾"},{"v":"5","n":"美国"},{"v":"18","n":"韩国"},{"v":"2","n":"日本"}]},{"key":"year","name":"年代","value":[{"v":"162","n":"2026"},{"v":"107","n":"2025"},{"v":"119","n":"2024"},{"v":"153","n":"2023"},{"v":"101","n":"2022"},{"v":"118","n":"2021"},{"v":"16","n":"2020"},{"v":"7","n":"2019"},{"v":"2","n":"2018"},{"v":"3","n":"2017"},{"v":"22","n":"2016"},{"v":"2015","n":"2015以前"}]},{"key":"sort","name":"排序","value":[{"v":"update","n":"最新"},{"v":"hot","n":"最热"},{"v":"rating","n":"评分"}]}],"3":[{"key":"cateId","name":"分类","value":[{"v":"1","n":"剧情"},{"v":"2","n":"爱情"},{"v":"3","n":"动画"},{"v":"4","n":"喜剧"},{"v":"5","n":"战争"},{"v":"6","n":"歌舞"},{"v":"7","n":"古装"},{"v":"8","n":"奇幻"},{"v":"9","n":"冒险"},{"v":"10","n":"动作"},{"v":"11","n":"科幻"},{"v":"12","n":"悬疑"},{"v":"13","n":"犯罪"},{"v":"14","n":"家庭"},{"v":"15","n":"传记"},{"v":"16","n":"运动"},{"v":"18","n":"惊悚"},{"v":"20","n":"短片"},{"v":"21","n":"历史"},{"v":"22","n":"音乐"},{"v":"23","n":"西部"},{"v":"24","n":"武侠"},{"v":"25","n":"恐怖"}]},{"key":"area","name":"地區","value":[{"v":"1","n":"国产"},{"v":"3","n":"中国香港"},{"v":"6","n":"中国台湾"},{"v":"5","n":"美国"},{"v":"18","n":"韩国"},{"v":"2","n":"日本"}]},{"key":"year","name":"年代","value":[{"v":"162","n":"2026"},{"v":"107","n":"2025"},{"v":"119","n":"2024"},{"v":"153","n":"2023"},{"v":"101","n":"2022"},{"v":"118","n":"2021"},{"v":"16","n":"2020"},{"v":"7","n":"2019"},{"v":"2","n":"2018"},{"v":"3","n":"2017"},{"v":"22","n":"2016"},{"v":"2015","n":"2015以前"}]},{"key":"sort","name":"排序","value":[{"v":"update","n":"最新"},{"v":"hot","n":"最热"},{"v":"rating","n":"评分"}]}],"4":[{"key":"cateId","name":"分类","value":[{"v":"1","n":"剧情"},{"v":"2","n":"爱情"},{"v":"3","n":"动画"},{"v":"4","n":"喜剧"},{"v":"5","n":"战争"},{"v":"6","n":"歌舞"},{"v":"7","n":"古装"},{"v":"8","n":"奇幻"},{"v":"9","n":"冒险"},{"v":"10","n":"动作"},{"v":"11","n":"科幻"},{"v":"12","n":"悬疑"},{"v":"13","n":"犯罪"},{"v":"14","n":"家庭"},{"v":"15","n":"传记"},{"v":"16","n":"运动"},{"v":"18","n":"惊悚"},{"v":"20","n":"短片"},{"v":"21","n":"历史"},{"v":"22","n":"音乐"},{"v":"23","n":"西部"},{"v":"24","n":"武侠"},{"v":"25","n":"恐怖"}]},{"key":"area","name":"地區","value":[{"v":"1","n":"国产"},{"v":"3","n":"中国香港"},{"v":"6","n":"中国台湾"},{"v":"5","n":"美国"},{"v":"18","n":"韩国"},{"v":"2","n":"日本"}]},{"key":"year","name":"年代","value":[{"v":"162","n":"2026"},{"v":"107","n":"2025"},{"v":"119","n":"2024"},{"v":"153","n":"2023"},{"v":"101","n":"2022"},{"v":"118","n":"2021"},{"v":"16","n":"2020"},{"v":"7","n":"2019"},{"v":"2","n":"2018"},{"v":"3","n":"2017"},{"v":"22","n":"2016"},{"v":"2015","n":"2015以前"}]},{"key":"sort","name":"排序","value":[{"v":"update","n":"最新"},{"v":"hot","n":"最热"},{"v":"rating","n":"评分"}]}]};

return JSON.stringify({ class: classes, filters: filterObj });
}

//主页推荐
async function homeVod() {
let html = JSON.parse(await request(`${host}/api/slide/list?pos_id=88`));

let videos = html.data.map(item => ({
    vod_id: item.jump_id,
    vod_name: item.title,
    vod_pic: `${imghost}${item.thumbnail}`,
    vod_remarks: "",
    style: {"type": "rect", "ratio": 1.485 }
 }))

return JSON.stringify({ list: videos });
}

//分类
async function category (tid, pg, filter, extend) {
let html = JSON.parse(await request(`${host}/api/crumb/list?fcate_pid=${tid}&category_id=&area=${extend.area || ''}&year=${extend.year || ''}&type=${extend.cateId || ''}&sort=${extend.sort || ''}&page=${pg}`));

let videos = html.data.map(item => ({
    vod_id: item.id,
    vod_name: item.title,
    vod_pic: `${imghost}${item.path}`,
    vod_remarks: item.mask,
    vod_year: ""
 }))

return JSON.stringify({ page: pg, pagecount: 99999, limit: videos.length, total: 99999, list: videos });
}

//详情
async function detail (id) {
let html = JSON.parse(await request(`${host}/api/video/detailv2?id=${id}`)).data

let arr = [];
//下载线路
if (html.ftp_list?.length > 0) {
    arr.push({
    "from" : '荐片',
    "url" : html.ftp_list.map(item => `${item.title}$${item.url}`).join('#')
    });
}
//vip线路
html.vip_source_list_source?.forEach(i => {
    let name = i.name; 
    let url = i.source_list?.map(item => `${item.source_name}$${item.url}`).join('#');
    arr.push({
    "from": name,
    "url": url
    });
});
let play_from = arr.map(item => item.from).join('$$$')
let play_url = arr.map(item => item.url).join('$$$')

var vod = {
    "type_name": html.category?.map(i =>  i.title).join(' / ') || '',
    "vod_year": html.year,
    "vod_area": html.area,
    "vod_remarks": html.mask,
    "vod_actor": '',
    "vod_director": '',
    "vod_content": html.description,
    "vod_play_from": play_from, 
    "vod_play_url": play_url
    }

return JSON.stringify({ list: [vod] })
}


//播放
async function play (flag, id, flags) {
if (id.indexOf(".m3u8") > -1){
  return JSON.stringify ({
    parse: 0,
    url: id
  });
}

return JSON.stringify({ parse: 0, url: `tvbox-xg:${id}` });
}

//搜索
async function search (wd, quick, pg=1) {
  let html = JSON.parse(await request(`${host}/api/v2/search/videoV2?key=${wd}&category_id=88&page=${pg}&pageSize=20`))
  let videos = html.data.map(item => ({
    vod_id: item.id,
    vod_name: item.title,
    vod_pic: `${imghost}${item.thumbnail}`,
    vod_remarks: item.mask,
    vod_year: ""
 }))

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