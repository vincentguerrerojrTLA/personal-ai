(() => {
  const clamp = n => Math.max(0, Math.min(100, Number.isFinite(Number(n)) ? Number(n) : 0));
  const round = n => Math.round(clamp(n));
  const escGauge = (v='') => String(v).replace(/[&<>"']/g, m => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[m]));

  function phaseSnapshot(){
    try{
      const phases = state?.phaseData?.phases || [];
      const currentCode = state?.phaseData?.current_phase;
      if(!phases.length) return null;
      const current = phases.find(p => p.code === currentCode) || phases.find(p => p.status === 'NEXT') || phases[0];
      const overall = phases.reduce((sum,p) => sum + clamp(p.progress), 0) / phases.length;
      const complete = phases.filter(p => clamp(p.progress) >= 100).length;
      return { phases, current, overall: round(overall), currentProgress: round(current?.progress || 0), complete, total: phases.length };
    }catch{return null;}
  }

  function ring(percent, label){
    return `<div class="progress-gauge" style="--p:${percent}"><div class="progress-gauge-value">${percent}<small>%</small></div></div>`;
  }

  function gaugePanel(snap, compact=false){
    const current = snap.current;
    return `<div class="progress-gauge-panel">
      <div class="progress-gauge-card">
        ${ring(snap.overall,'Overall build')}
        <div class="progress-gauge-copy">
          <b>Overall Build Progress</b>
          <strong>${snap.complete} of ${snap.total} major phases fully complete</strong>
          <p>The overall percentage is the average of every major phase's recorded progress. Completed phases remain at 100%; active and future phases contribute their live percentage.</p>
          <span class="gauge-note">Calculated from data/phases.json · live with GitHub updates</span>
        </div>
      </div>
      <div class="progress-gauge-card current">
        ${ring(snap.currentProgress,'Current phase')}
        <div class="progress-gauge-copy">
          <b>Current Phase Progress</b>
          <strong>Phase ${escGauge(current.number)} · ${escGauge(current.title)}</strong>
          <p>${escGauge(current.summary)}</p>
          <span class="gauge-note">${escGauge(current.status)} · ${escGauge(current.range)}</span>
        </div>
      </div>
    </div>`;
  }

  function ensureHomeGauges(snap){
    const home = document.querySelector('[data-screen="home"]');
    const phaseBlock = home?.querySelector('.home-phase-block');
    if(!home || !phaseBlock) return;
    let shell = home.querySelector('.home-gauge-shell');
    if(!shell){
      shell = document.createElement('div');
      shell.className = 'home-gauge-shell';
      phaseBlock.parentNode.insertBefore(shell, phaseBlock);
    }
    const key = `${snap.overall}|${snap.currentProgress}|${snap.current.code}`;
    if(shell.dataset.key !== key){ shell.dataset.key = key; shell.innerHTML = gaugePanel(snap); }
  }

  function ensurePhaseSummary(snap){
    const screen = document.querySelector('[data-screen="phases"]');
    const rail = screen?.querySelector('#phaseRail');
    if(!screen || !rail) return;
    let shell = screen.querySelector('.phase-summary-gauges');
    if(!shell){
      shell = document.createElement('div');
      shell.className = 'home-gauge-shell phase-summary-gauges';
      rail.parentNode.insertBefore(shell, rail);
    }
    const key = `${snap.overall}|${snap.currentProgress}|${snap.current.code}`;
    if(shell.dataset.key !== key){ shell.dataset.key = key; shell.innerHTML = gaugePanel(snap,true); }
  }

  function ensureInlinePhaseGauges(snap){
    for(const p of snap.phases){
      const section = document.getElementById(`phase-${p.code}`);
      if(!section) continue;
      const main = section.querySelector('.phase-main');
      const progress = main?.querySelector('.phase-progress');
      if(!main || !progress) continue;
      let inline = main.querySelector('.phase-inline-gauge');
      if(!inline){
        inline = document.createElement('div');
        inline.className = 'phase-inline-gauge';
        progress.parentNode.insertBefore(inline, progress);
      }
      const pct = round(p.progress);
      const key = `${pct}|${p.status}`;
      if(inline.dataset.key !== key){
        inline.dataset.key = key;
        inline.innerHTML = `<div class="phase-inline-ring" style="--p:${pct}"><span>${pct}%</span></div><div class="phase-inline-copy"><b>Phase ${escGauge(p.number)} Progress</b><small>${escGauge(p.status)} · ${pct === 100 ? 'phase closed' : pct === 0 ? 'not yet advanced' : 'work in progress'}</small></div>`;
      }
    }
  }

  function renderGauges(){
    const snap = phaseSnapshot();
    if(!snap) return;
    ensureHomeGauges(snap);
    ensurePhaseSummary(snap);
    ensureInlinePhaseGauges(snap);
  }

  if(document.readyState === 'loading') document.addEventListener('DOMContentLoaded', renderGauges);
  else renderGauges();

  setInterval(renderGauges, 1000);
})();
