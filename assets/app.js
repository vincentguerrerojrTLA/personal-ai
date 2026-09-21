const RAW_DATA_URL = 'https://raw.githubusercontent.com/vincentguerrerojrTLA/personal-ai/main/data/roadmap.json';
const LOCAL_DATA_URL = './data/roadmap.json';
const state = { data:null, screen:'home', filter:'ALL', query:'', selected:null, connected:false, lastFetch:null };

const $ = (s,root=document)=>root.querySelector(s);
const $$ = (s,root=document)=>[...root.querySelectorAll(s)];
const esc = (v='') => String(v).replace(/[&<>'"]/g,m=>({ '&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','"':'&quot;' }[m]));
const fmtTime = iso => { try { return new Date(iso).toLocaleString([], {month:'short',day:'numeric',hour:'numeric',minute:'2-digit'}); } catch { return iso; } };
const statusClass = s => String(s||'').toLowerCase().replace(/[^a-z0-9]+/g,'-');

async function loadData(initial=false){
  const url = initial ? LOCAL_DATA_URL : `${RAW_DATA_URL}?t=${Date.now()}`;
  try{
    const r = await fetch(url,{cache:'no-store'});
    if(!r.ok) throw new Error(`HTTP ${r.status}`);
    const data = await r.json();
    const oldStamp = state.data?.meta?.updated_at;
    state.data = data;
    state.connected = true;
    state.lastFetch = new Date();
    if(!state.selected) state.selected = data.current.focus_code;
    renderAll();
    updateLive();
    if(oldStamp && oldStamp !== data.meta.updated_at) flashUpdate();
  }catch(err){
    if(!state.data && !initial){
      try{
        const r=await fetch(LOCAL_DATA_URL,{cache:'no-store'}); state.data=await r.json(); state.connected=false; state.lastFetch=new Date(); renderAll(); updateLive();
      }catch{}
    }else{ state.connected=false; state.lastFetch=new Date(); updateLive(); }
    console.warn('Roadmap refresh failed:',err);
  }
}

function flashUpdate(){
  const el=$('#liveIndicator');
  if(!el) return;
  el.animate([{boxShadow:'0 0 0 rgba(98,231,196,0)'},{boxShadow:'0 0 28px rgba(98,231,196,.55)'},{boxShadow:'0 0 0 rgba(98,231,196,0)'}],{duration:1000});
}

function navigate(screen, selected=null){
  state.screen=screen;
  if(selected) state.selected=selected;
  $$('.screen').forEach(x=>x.classList.toggle('active',x.dataset.screen===screen));
  $$('.nav button,.mobile-drawer button').forEach(x=>x.classList.toggle('active',x.dataset.nav===screen));
  if(screen==='roadmap') renderRoadmap();
  history.replaceState(null,'',selected?`#${screen}/${encodeURIComponent(selected)}`:`#${screen}`);
  window.scrollTo({top:0,behavior:'smooth'});
  $('#mobileDrawer')?.classList.remove('open');
}

function parseHash(){
  const raw=location.hash.slice(1); if(!raw) return;
  const [screen,item]=raw.split('/');
  if(['home','roadmap','systems','agents','integrations','activity'].includes(screen)){
    state.screen=screen; if(item) state.selected=decodeURIComponent(item);
  }
}

function renderAll(){
  if(!state.data) return;
  renderHeader(); renderHome(); renderRoadmap(); renderSystems(); renderAgents(); renderIntegrations(); renderActivity();
  navigate(state.screen,state.selected);
}

function renderHeader(){
  const d=state.data;
  $('#headerRelease').textContent=d.current.release;
  $('#brandVersion').textContent=`${d.current.release} · GITHUB COMMAND CENTER`;
}

function renderHome(){
  const d=state.data, c=d.current;
  $('#heroTitle').innerHTML=`${esc(c.headline.split(',')[0])}<span>${esc(c.headline.includes(',')?c.headline.split(',').slice(1).join(',').trim():'')}</span>`;
  if(!c.headline.includes(',')) $('#heroTitle').innerHTML=`THE BUILD<span>IN MOTION</span>`;
  $('#heroCopy').textContent=c.summary;
  $('#principles').innerHTML=c.principles.map((p,i)=>`<div class="principle"><b>0${i+1}</b>${esc(p)}</div>`).join('');
  $('#releaseName').textContent=c.release;
  $('#focusTitle').textContent=c.focus_title;
  $('#focusSummary').textContent=(d.roadmap.find(x=>x.code===c.focus_code)||{}).summary||'';
  const fp=Math.round((c.foundation_complete/c.foundation_total)*100);
  $('#foundationLabel').textContent=`${c.foundation_complete} / ${c.foundation_total}`;
  $('#foundationBar').style.width=`${fp}%`;
  $('#v1Label').textContent=`${c.v1_complete} COMPLETE · ${c.v1_next} NEXT`;
  const knownV1=Math.max(4,c.v1_complete+1); $('#v1Bar').style.width=`${Math.round(c.v1_complete/knownV1*100)}%`;
  $('#statusGrid').innerHTML=d.status.map(s=>`<div class="status-mini tone-${esc(s.tone)}"><span>${esc(s.label)}</span><b>${esc(s.value)}</b></div>`).join('');
  $('#homeUpdated').textContent=fmtTime(d.meta.updated_at);
}

function groupRoadmap(items){
  return items.reduce((a,x)=>{(a[x.phase]??=[]).push(x);return a;},{});
}
function roadmapFiltered(){
  const q=state.query.trim().toLowerCase();
  return state.data.roadmap.filter(x=>{
    const f=state.filter==='ALL'||x.status===state.filter;
    const hay=`${x.code} ${x.title} ${x.summary} ${x.objective} ${x.evidence}`.toLowerCase();
    return f && (!q || hay.includes(q));
  });
}
function renderRoadmap(){
  if(!state.data) return;
  const items=roadmapFiltered(), groups=groupRoadmap(items), list=$('#roadmapList');
  const selected=state.data.roadmap.find(x=>x.code===state.selected) || items[0] || state.data.roadmap[0];
  if(selected && !state.selected) state.selected=selected.code;
  if(!items.length){ list.innerHTML='<div class="empty">NO ROADMAP ITEMS MATCH THIS VIEW.</div>'; }
  else{
    list.innerHTML=Object.entries(groups).map(([phase,arr])=>`<div class="phase-group"><div class="phase-label">${esc(phase)}</div>${arr.map(x=>`
      <button class="roadmap-item ${x.code===state.selected?'active':''}" data-roadmap="${esc(x.code)}">
        <span class="roadmap-code">${esc(x.code.replace('GATE ','G'))}</span>
        <span class="roadmap-title">${esc(x.title)}<small>${esc(x.summary.slice(0,78))}${x.summary.length>78?'…':''}</small></span>
        <span class="badge ${statusClass(x.status)}">${esc(x.status)}</span>
      </button>`).join('')}</div>`).join('');
  }
  $$('[data-roadmap]',list).forEach(b=>b.onclick=()=>{state.selected=b.dataset.roadmap;renderRoadmap();history.replaceState(null,'',`#roadmap/${encodeURIComponent(state.selected)}`)});
  renderRoadmapDetail(selected);
  const counts=state.data.roadmap.reduce((a,x)=>(a[x.status]=(a[x.status]||0)+1,a),{});
  $('#roadmapCounts').textContent=`${counts.COMPLETE||0} COMPLETE · ${counts.NEXT||0} NEXT · ${counts.PLANNED||0} PLANNED`;
}

function renderRoadmapDetail(x){
  const d=$('#roadmapDetail'); if(!x){d.innerHTML='<div class="empty">SELECT A ROADMAP ITEM.</div>';return;}
  d.innerHTML=`
    <div class="eyebrow">${esc(x.phase)} · ${esc(x.code)}</div>
    <h3>${esc(x.title)}</h3>
    <span class="badge ${statusClass(x.status)}">${esc(x.status)}</span>
    <p class="detail-summary">${esc(x.summary)}</p>
    <div class="detail-grid">
      <div class="detail-block"><b>Objective</b><span>${esc(x.objective||'Not scheduled yet.')}</span></div>
      <div class="detail-block"><b>Unlocked capability</b><span>${esc(x.unlocked||'Pending.')}</span></div>
      <div class="detail-block wide evidence"><b>Verification / evidence</b><span>${esc(x.evidence||'Verification begins when this milestone enters production.')}</span></div>
      <div class="detail-block wide"><b>Next action</b><span>${esc(x.next_action||'No additional action recorded.')}</span></div>
    </div>`;
}

function renderSystems(){
  $('#systemsGrid').innerHTML=state.data.systems.map(x=>`
    <article class="data-card"><span class="badge ${x.verification==='PASS'?'pass':'info'}">${esc(x.status)}</span><h3>${esc(x.name)}</h3><p>${esc(x.purpose)}</p><div class="card-facts"><div class="fact"><b>Latest verification</b><span>${esc(x.verification)}</span></div><div class="fact"><b>Safety boundary</b><span>${esc(x.boundary)}</span></div></div></article>`).join('');
}
function renderAgents(){
  $('#agentsGrid').innerHTML=state.data.agents.map(x=>`
    <article class="data-card agent"><span class="badge ${x.status.includes('VALIDATED')?'complete':'info'}">${esc(x.status)}</span><h3>${esc(x.name)}</h3><p>${esc(x.scope)}</p><div class="fact"><b>Governance</b><span>Inherits protected owner rules and scoped permissions.</span></div></article>`).join('');
}
function renderIntegrations(){
  $('#integrationsGrid').innerHTML=state.data.integrations.map(x=>`
    <article class="data-card"><span class="badge ${statusClass(x.status)}">${esc(x.status)}</span><h3>${esc(x.name)}</h3><p>${esc(x.detail)}</p></article>`).join('');
}
function renderActivity(){
  const items=[...state.data.activity].sort((a,b)=>new Date(b.time)-new Date(a.time));
  $('#activityFeed').innerHTML=items.map(x=>`
    <div class="activity-entry"><div class="activity-time">${esc(fmtTime(x.time))}</div><div class="activity-type"><span class="badge ${statusClass(x.status)}">${esc(x.type)}</span></div><div class="activity-body"><strong>${esc(x.title)}</strong><p>${esc(x.detail)}</p></div></div>`).join('');
  $('#activityCount').textContent=`${items.length} RECORDED UPDATES`;
  $('#activityCurrent').textContent=state.data.current.focus_title;
}

function updateLive(){
  const ind=$('#liveIndicator'), text=$('#liveText'), last=$('#lastFetch'), source=$('#dataSource'), stamp=$('#sourceUpdated');
  if(ind) ind.classList.toggle('live',state.connected);
  if(text) text.textContent=state.connected?'LIVE / CONNECTED':'LOCAL FALLBACK';
  if(last) last.textContent=state.lastFetch?state.lastFetch.toLocaleTimeString([],{hour:'numeric',minute:'2-digit',second:'2-digit'}):'—';
  if(source) source.textContent=state.connected?'GitHub main / roadmap.json':'Bundled local copy';
  if(stamp) stamp.textContent=state.data?fmtTime(state.data.meta.updated_at):'—';
}

function bindUI(){
  $$('.nav button,.mobile-drawer button').forEach(b=>b.onclick=()=>navigate(b.dataset.nav));
  $('#menuBtn').onclick=()=>$('#mobileDrawer').classList.toggle('open');
  $('#viewRoadmap').onclick=()=>navigate('roadmap',state.data?.current.focus_code);
  $('#viewActivity').onclick=()=>navigate('activity');
  $$('.filter').forEach(b=>b.onclick=()=>{state.filter=b.dataset.filter;$$('.filter').forEach(x=>x.classList.toggle('active',x===b));renderRoadmap()});
  $('#roadmapSearch').addEventListener('input',e=>{state.query=e.target.value;renderRoadmap()});
  window.addEventListener('hashchange',()=>{parseHash();navigate(state.screen,state.selected)});
}

function initNetwork(){
  const c=$('#network'),ctx=c.getContext('2d'); let w,h,nodes=[];
  function resize(){w=c.width=innerWidth*devicePixelRatio;h=c.height=innerHeight*devicePixelRatio;c.style.width=innerWidth+'px';c.style.height=innerHeight+'px';nodes=Array.from({length:Math.min(55,Math.max(24,Math.floor(innerWidth/30)))},()=>({x:Math.random()*w,y:Math.random()*h,vx:(Math.random()-.5)*.12*devicePixelRatio,vy:(Math.random()-.5)*.12*devicePixelRatio,r:(Math.random()*1.2+.4)*devicePixelRatio}))}
  function frame(){ctx.clearRect(0,0,w,h);ctx.lineWidth=.55*devicePixelRatio;for(let i=0;i<nodes.length;i++){const a=nodes[i];a.x+=a.vx;a.y+=a.vy;if(a.x<0||a.x>w)a.vx*=-1;if(a.y<0||a.y>h)a.vy*=-1;for(let j=i+1;j<nodes.length;j++){const b=nodes[j],dx=a.x-b.x,dy=a.y-b.y,dist=Math.hypot(dx,dy);if(dist<150*devicePixelRatio){ctx.strokeStyle=`rgba(62,137,255,${.12*(1-dist/(150*devicePixelRatio))})`;ctx.beginPath();ctx.moveTo(a.x,a.y);ctx.lineTo(b.x,b.y);ctx.stroke()}}ctx.fillStyle='rgba(89,217,255,.34)';ctx.beginPath();ctx.arc(a.x,a.y,a.r,0,Math.PI*2);ctx.fill()}requestAnimationFrame(frame)}
  addEventListener('resize',resize);resize();frame();
}

parseHash();
bindUI();initNetwork();
loadData(true).then(()=>setTimeout(()=>loadData(false),1200));
setInterval(()=>loadData(false),15000);
