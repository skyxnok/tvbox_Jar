const siteConf = {
  site_name: "稀饭动漫",
  data: {
    item_xpath:      `//div[@class="vod-rank-box"][1]/div/div[2]/a`,      
    list: {
      vod_id:           `//a/@href`,
      vod_name:     `//a/@title`,
      vod_pic:         `//img/@data-src`,
      vod_remarks: `//div[@class="vod-rank-title-box flex-auto"]/div[2]/text()`,
//      vod_year:       ``,
//      style: {"type": "rect", "ratio": 1.485 }
    }
  },
  detail: {
    info: {
      type_name:     ``,
      vod_year:        ``,
      vod_area:        ``,
      vod_actor:       `//div[@class="detail-info rel flex-auto wow lightSpeedIn"]/div[3]/a`,
      vod_director:   `//div[@class="detail-info rel flex-auto wow lightSpeedIn"]/div[2]/a`,
      vod_remarks:   `//div[@class="detail-info rel flex-auto wow lightSpeedIn"]/div[1]/span[1]`,
      vod_content:   `//meta[@name="description"]/@content`
    },
    play: {
      roadName: `//div[@class="swiper-wrapper"]/a/text()`,
      roads:         `//div[@class="anthology wow fadeInUp animated"]//ul`,
      ep: { name: `//a/text()`, url: `//a/@href` }
    }
  },
  tags: {
    item_xpath:   `//div[@class="single-video-tag"]`,
    list: { id: `//a/@href`, name: `//a/text()` }
  }
};


let host;

async function init(cfg) {
host = cfg.ext.host || "https://anime.xifanacg.com"
}

async function request(reqUrl) {
  const res = await req(reqUrl, {
    headers: {
      'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.60 Safari/537.36 Edg/100.0.1185.29',
      'Referer': host
    }
  });
  return res.content;
}

//分类
async function home (filter) {
let html = await request(host+'/label/rank.html');

//一级
let classes = [{"type_id":"1","type_name":"连载新番"},{"type_id":"2","type_name":"完结旧番"},{"type_id":"3","type_name":"剧场版"},{"type_id":"21","type_name":"美漫"}]

let filterObj = {"1":[{"key":"year","name":"年份","value":[{"n":"全部","v":""},{"n":"2026","v":"2026"},{"n":"2025","v":"2025"},{"n":"2024","v":"2024"},{"n":"2023","v":"2023"},{"n":"2022","v":"2022"},{"n":"2021","v":"2021"},{"n":"2020","v":"2020"},{"n":"2019","v":"2019"},{"n":"2018","v":"2018"},{"n":"2017","v":"2017"},{"n":"2016","v":"2016"},{"n":"2015","v":"2015"},{"n":"2014","v":"2014"},{"n":"2013","v":"2013"},{"n":"2012","v":"2012"},{"n":"2011","v":"2011"},{"n":"2010","v":"2010"},{"n":"2009","v":"2009"},{"n":"2008","v":"2008"},{"n":"2006","v":"2006"},{"n":"2005","v":"2005"}]},{"key":"sort","name":"排序","value":[{"n":"最新","v":"time"},{"n":"最热","v":"hits"},{"n":"评分","v":"score"}]}],"2":[{"key":"year","name":"年份","value":[{"n":"全部","v":""},{"n":"2026","v":"2026"},{"n":"2025","v":"2025"},{"n":"2024","v":"2024"},{"n":"2023","v":"2023"},{"n":"2022","v":"2022"},{"n":"2021","v":"2021"},{"n":"2020","v":"2020"},{"n":"2019","v":"2019"},{"n":"2018","v":"2018"},{"n":"2017","v":"2017"},{"n":"2016","v":"2016"},{"n":"2015","v":"2015"},{"n":"2014","v":"2014"},{"n":"2013","v":"2013"},{"n":"2012","v":"2012"},{"n":"2011","v":"2011"},{"n":"2010","v":"2010"},{"n":"2009","v":"2009"},{"n":"2008","v":"2008"},{"n":"2006","v":"2006"},{"n":"2005","v":"2005"}]},{"key":"sort","name":"排序","value":[{"n":"最新","v":"time"},{"n":"最热","v":"hits"},{"n":"评分","v":"score"}]}],"3":[{"key":"year","name":"年份","value":[{"n":"全部","v":""},{"n":"2026","v":"2026"},{"n":"2025","v":"2025"},{"n":"2024","v":"2024"},{"n":"2023","v":"2023"},{"n":"2022","v":"2022"},{"n":"2021","v":"2021"},{"n":"2020","v":"2020"},{"n":"2019","v":"2019"},{"n":"2018","v":"2018"},{"n":"2017","v":"2017"},{"n":"2016","v":"2016"},{"n":"2015","v":"2015"},{"n":"2014","v":"2014"},{"n":"2013","v":"2013"},{"n":"2012","v":"2012"},{"n":"2011","v":"2011"},{"n":"2010","v":"2010"},{"n":"2009","v":"2009"},{"n":"2008","v":"2008"},{"n":"2006","v":"2006"},{"n":"2005","v":"2005"}]},{"key":"sort","name":"排序","value":[{"n":"最新","v":"time"},{"n":"最热","v":"hits"},{"n":"评分","v":"score"}]}],"21":[{"key":"year","name":"年份","value":[{"n":"全部","v":""},{"n":"2026","v":"2026"},{"n":"2025","v":"2025"},{"n":"2024","v":"2024"},{"n":"2023","v":"2023"},{"n":"2022","v":"2022"},{"n":"2021","v":"2021"},{"n":"2020","v":"2020"},{"n":"2019","v":"2019"},{"n":"2018","v":"2018"},{"n":"2017","v":"2017"},{"n":"2016","v":"2016"},{"n":"2015","v":"2015"},{"n":"2014","v":"2014"},{"n":"2013","v":"2013"},{"n":"2012","v":"2012"},{"n":"2011","v":"2011"},{"n":"2010","v":"2010"},{"n":"2009","v":"2009"},{"n":"2008","v":"2008"},{"n":"2006","v":"2006"},{"n":"2005","v":"2005"}]},{"key":"sort","name":"排序","value":[{"n":"最新","v":"time"},{"n":"最热","v":"hits"},{"n":"评分","v":"score"}]}]}

let videos = xpathList(html, siteConf.data.item_xpath, siteConf.data.list);

return JSON.stringify({ class: classes, filters: filterObj, list: videos });
}


//主页推荐
async function homeVod() {
}

//分类
async function category (tid, pg, filter, extend) {
let t = Math.floor(Date.now() / 1000);
let key = md5X(`DS${t}DCC147D11943AF75`)

let html = JSON.parse((await req(`${host}/index.php/api/vod`, {
        body: `type=${tid}&class=&area=&year=${extend.year || ""}&lang=&version=&state=&letter=&time=&level=0&weekday=&by=${extend.sort || "time"}&page=${pg}&time=${t}&key=${key}`,
        headers: {"Content-Type": "application/x-www-form-urlencoded; charset=UTF-8"},
        method: 'POST'
    })).content)

let videos = html.list.map(item => ({
    vod_id: `/bangumi/${item.vod_id}.html`,
    vod_name: item.vod_name,
    vod_pic: item.vod_pic,
    vod_remarks: item.vod_remarks
}));

return JSON.stringify({ page: pg, pagecount: 99999, limit: videos.length, total: 99999, list: videos });
}

//详情
async function detail (id) {
let html = await request(`${host}${id}`);
let vod = xpathVod(html, siteConf.detail.info, siteConf.detail.play)
for (const k of Object.keys(vod)) {
    if (k.startsWith('vod_') && Array.isArray(vod[k])) {
        vod[k] = vod[k].filter(item => item && String(item).trim()).join(' / ');
    }
}

return JSON.stringify({ list: [vod] })
}


//播放
async function play (flag, id, flags) {
let html = await request(`${host}${id}`);
let url = JSON.parse(html.match(/var player_aaaa=(.*?)</)[1]).url

return JSON.stringify({ parse: 0, url: url })
}

//搜索
async function search (wd, quick, pg=1) {

let html = await request(`${host}/index.php/ajax/suggest?mid=1&wd=${wd}&limit=500`);
let videos = JSON.parse(html).list.map(item => ({
    vod_id: `/bangumi/${item.id}.html`,
    vod_name: item.name,
    vod_pic: item.pic
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