(() => {
  const byId = (id) => document.getElementById(id);
  const notice = byId('notice');
  const householdList = byId('household-list');
  const challengeList = byId('challenge-list');
  const activityTable = byId('activity-table');
  const participationTable = byId('participation-table');
  const leaderboardTable = byId('leaderboard-table');
  const dashboardHouseholds = byId('dashboard-households');
  const dashboardActivities = byId('dashboard-activities');
  const dashboardChallenges = byId('dashboard-challenges');
  const today = new Date();
  const localDate = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`;
  const monthNow = localDate.slice(0, 7);
  let households = [];
  let challenges = [];
  let noticeTimer;

  document.querySelectorAll('input[type="month"]').forEach((input) => { input.value = monthNow; });
  document.querySelector('#activity-form [name="activityDate"]').value = localDate;

  const escapeHtml = (value) => String(value ?? '').replace(/[&<>"']/g, (character) => ({
    '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
  })[character]);

  const formatNumber = (value, digits = 2) => Number(value ?? 0).toLocaleString(undefined, {
    minimumFractionDigits: 0,
    maximumFractionDigits: digits
  });

  function showNotice(message, type = 'success') {
    clearTimeout(noticeTimer);
    notice.className = `notice ${type}`;
    notice.textContent = message;
    notice.hidden = false;
    noticeTimer = setTimeout(() => { notice.hidden = true; }, 5500);
  }

  async function api(url, options = {}) {
    let response;
    try {
      response = await fetch(url, {
        ...options,
        headers: { ...(options.body ? { 'Content-Type': 'application/json' } : {}), ...options.headers }
      });
    } catch {
      throw new Error('The CarbonTrack service could not be reached. Check that the Spring Boot application is running.');
    }

    if (response.status === 204) return null;
    const contentType = response.headers.get('content-type') || '';
    const body = contentType.includes('application/json') ? await response.json().catch(() => null) : null;
    if (!response.ok) {
      const fieldMessages = body?.fields && typeof body.fields === 'object'
        ? Object.entries(body.fields).map(([field, message]) => `${field}: ${message}`).join(' ')
        : '';
      const message = [body?.error, fieldMessages].filter(Boolean).join('. ')
        || (response.status === 404 ? 'The requested record could not be found.'
          : response.status === 400 ? 'Please check the submitted information and try again.'
            : `The request could not be completed (HTTP ${response.status}).`);
      throw new Error(message);
    }
    return body;
  }

  function populateHouseholdSelects() {
    document.querySelectorAll('[data-household-select]').forEach((select) => {
      const previous = select.value;
      const prompt = select.hasAttribute('data-participation-select') ? 'Choose a household' : 'Select a household';
      select.innerHTML = `<option value="">${prompt}</option>${households.map((household) =>
        `<option value="${escapeHtml(household.id)}">${escapeHtml(household.name)}</option>`).join('')}`;
      if (households.some((household) => String(household.id) === previous)) select.value = previous;
    });
    document.querySelectorAll('.challenge-household-select').forEach((select) => {
      const previous = select.value;
      select.innerHTML = `<option value="">Household</option>${households.map((household) =>
        `<option value="${escapeHtml(household.id)}">${escapeHtml(household.name)}</option>`).join('')}`;
      if (households.some((household) => String(household.id) === previous)) select.value = previous;
    });
  }

  async function loadHouseholds() {
    householdList.innerHTML = '<p class="loading-text">Loading households…</p>';
    try {
      households = await api('/api/households') || [];
      byId('household-total').textContent = households.length;
      populateHouseholdSelects();
      householdList.innerHTML = households.length ? households.map((household) => `
        <article class="household-row">
          <div><div class="household-name">${escapeHtml(household.name)}</div><div class="household-meta">${escapeHtml(household.membersCount)} ${Number(household.membersCount) === 1 ? 'member' : 'members'}</div></div>
          <span class="household-id">#${escapeHtml(household.id)}</span>
        </article>`).join('') : '<p class="empty-state">No households yet. Create one to get started.</p>';
      const selected = document.querySelector('#activity-form [name="householdId"]').value;
      if (selected) await loadActivities(selected);
      const participationHousehold = document.querySelector('[data-participation-select]').value;
      if (participationHousehold) await loadParticipations(participationHousehold);
      await loadDashboardData();
    } catch (error) {
      householdList.innerHTML = '<p class="empty-state">Households could not be loaded.</p>';
      showNotice(error.message, 'error');
    }
  }

  async function loadDashboardData() {
    byId('dashboard-month-label').textContent = monthNow;
    if (!households.length) {
      byId('activity-total').textContent = '0';
      byId('dashboard-footprint').textContent = '0';
      dashboardHouseholds.innerHTML = '<tr><td colspan="3" class="empty-state">No households found. Create a household to get started.</td></tr>';
      dashboardActivities.innerHTML = '<tr><td colspan="5" class="empty-state">No activities to show yet.</td></tr>';
      return;
    }

    dashboardHouseholds.innerHTML = '<tr><td colspan="3" class="empty-state">Loading household footprints…</td></tr>';
    dashboardActivities.innerHTML = '<tr><td colspan="5" class="empty-state">Loading activities…</td></tr>';
    const results = await Promise.all(households.map(async (household) => {
      const [activitiesResult, footprintResult] = await Promise.allSettled([
        api(`/api/households/${encodeURIComponent(household.id)}/activities`),
        api(`/api/households/${encodeURIComponent(household.id)}/footprint?month=${encodeURIComponent(monthNow)}`)
      ]);
      return {
        household,
        activities: activitiesResult.status === 'fulfilled' ? activitiesResult.value : null,
        footprint: footprintResult.status === 'fulfilled' ? footprintResult.value : null,
        failed: activitiesResult.status === 'rejected' || footprintResult.status === 'rejected'
      };
    }));

    const nameById = new Map(households.map((household) => [String(household.id), household.name]));
    const allActivities = results.flatMap((result) => (result.activities || []).map((activity) => ({
      ...activity,
      householdName: nameById.get(String(activity.householdId)) || result.household.name
    }))).sort((left, right) => String(right.activityDate).localeCompare(String(left.activityDate)));
    const totalFootprint = results.reduce((total, result) => total + Number(result.footprint?.totalCarbonEmissionKg || 0), 0);
    const hasFailures = results.some((result) => result.failed);

    byId('activity-total').textContent = allActivities.length;
    byId('dashboard-footprint').textContent = formatNumber(totalFootprint);
    dashboardHouseholds.innerHTML = results.map(({ household, footprint }) => `
      <tr><td><strong>${escapeHtml(household.name)}</strong></td><td>${escapeHtml(household.membersCount)}</td>
      <td><strong>${footprint ? `${formatNumber(footprint.totalCarbonEmissionKg)} kg` : 'Unavailable'}</strong></td></tr>`).join('');
    dashboardActivities.innerHTML = allActivities.length ? allActivities.slice(0, 8).map((activity) => `
      <tr><td><strong>${escapeHtml(activity.householdName)}</strong></td><td>${escapeHtml(activity.activityDate)}</td>
      <td><span class="activity-tag">${escapeHtml(activityLabel(activity.activityType))}</span></td>
      <td>${formatNumber(activity.quantity)} ${escapeHtml(activity.activityType === 'ELECTRICITY' ? 'kWh' : activity.activityType === 'WASTE' ? 'kg' : 'km')}</td>
      <td><strong>${formatNumber(activity.carbonEmissionKg)} kg</strong></td></tr>`).join('')
      : '<tr><td colspan="5" class="empty-state">No activities found. Record an activity to see it here.</td></tr>';
    if (hasFailures) showNotice('Some household data could not be loaded. Use the refresh controls to try again.', 'error');
  }

  function activityLabel(type) {
    return ({ CAR_COMMUTE: 'Car commute', BUS_COMMUTE: 'Bus commute', ELECTRICITY: 'Electricity', WASTE: 'Waste' })[type] || type;
  }

  async function loadActivities(householdId) {
    if (!householdId) {
      activityTable.innerHTML = '<tr><td colspan="4" class="empty-state">Choose a household to load activities.</td></tr>';
      return;
    }
    activityTable.innerHTML = '<tr><td colspan="4" class="empty-state">Loading activities…</td></tr>';
    try {
      const activities = await api(`/api/households/${encodeURIComponent(householdId)}/activities`);
      activityTable.innerHTML = activities.length ? activities.map((activity) => `
        <tr><td>${escapeHtml(activity.activityDate)}</td><td><span class="activity-tag">${escapeHtml(activityLabel(activity.activityType))}</span></td>
        <td>${formatNumber(activity.quantity)} ${escapeHtml(activity.activityType === 'ELECTRICITY' ? 'kWh' : activity.activityType === 'WASTE' ? 'kg' : 'km')}</td>
        <td><strong>${formatNumber(activity.carbonEmissionKg)} kg</strong></td></tr>`).join('')
        : '<tr><td colspan="4" class="empty-state">No activities recorded for this household.</td></tr>';
    } catch (error) {
      activityTable.innerHTML = '<tr><td colspan="4" class="empty-state">Activities could not be loaded.</td></tr>';
      showNotice(error.message, 'error');
    }
  }

  async function loadChallenges() {
    challengeList.innerHTML = '<p class="loading-text">Loading challenges…</p>';
    try {
      challenges = await api('/api/challenges') || [];
      const activeChallenges = challenges.filter((challenge) => challenge.startDate <= localDate && challenge.endDate >= localDate);
      byId('challenge-total').textContent = activeChallenges.length;
      dashboardChallenges.innerHTML = activeChallenges.length ? activeChallenges.map((challenge) => `
        <article class="active-challenge-item"><strong>${escapeHtml(challenge.name)}</strong>
        <p>${escapeHtml(challenge.description || 'No description provided.')}</p>
        <div class="active-challenge-meta">${formatNumber(challenge.targetReductionPercentage)}% target · ${escapeHtml(challenge.startDate)} to ${escapeHtml(challenge.endDate)}</div></article>`).join('')
        : '<p class="empty-state">No active challenges today.</p>';
      challengeList.innerHTML = challenges.length ? challenges.map((challenge) => `
        <article class="challenge-card">
          <div><h4>${escapeHtml(challenge.name)}</h4><p>${escapeHtml(challenge.description || 'No description provided.')}</p>
          <div class="challenge-details">${escapeHtml(challenge.targetReductionPercentage)}% reduction · ${escapeHtml(challenge.startDate)} to ${escapeHtml(challenge.endDate)}</div></div>
          <div class="challenge-action"><select class="challenge-household-select" aria-label="Household for ${escapeHtml(challenge.name)}"><option value="">Household</option></select>
          <button class="button button-primary join-button" type="button" data-challenge-id="${escapeHtml(challenge.id)}">Join challenge</button></div>
        </article>`).join('') : '<p class="empty-state">No challenges are available. Create the first one.</p>';
      populateHouseholdSelects();
    } catch (error) {
      challengeList.innerHTML = '<p class="empty-state">Challenges could not be loaded.</p>';
      dashboardChallenges.innerHTML = '<p class="empty-state">Challenges could not be loaded.</p>';
      showNotice(error.message, 'error');
    }
  }

  async function loadParticipations(householdId) {
    if (!householdId) {
      participationTable.innerHTML = '<tr><td colspan="5" class="empty-state">Choose a household to view joined challenges.</td></tr>';
      return;
    }
    participationTable.innerHTML = '<tr><td colspan="5" class="empty-state">Loading joined challenges…</td></tr>';
    try {
      const entries = await api(`/api/households/${encodeURIComponent(householdId)}/challenges`);
      participationTable.innerHTML = entries.length ? entries.map((entry) => {
        const achieved = Boolean(entry.targetAchieved);
        const reduction = Number(entry.reductionPercentage || 0);
        return `<tr><td><strong>${escapeHtml(entry.challengeName)}</strong></td><td>${formatNumber(entry.targetReductionPercentage)}%</td>
          <td>${formatNumber(entry.previousMonthFootprintKg)} kg</td><td>${formatNumber(entry.currentMonthFootprintKg)} kg</td>
          <td><span class="reduction-badge ${reduction < 0 ? 'negative' : ''}">${formatNumber(reduction)}%</span></td>
          <td><span class="status-badge ${achieved ? 'achieved' : 'in-progress'}">${achieved ? 'Target achieved' : 'Not achieved'}</span></td></tr>`;
      }).join('') : '<tr><td colspan="6" class="empty-state">This household has not joined any challenges yet.</td></tr>';
    } catch (error) {
      participationTable.innerHTML = '<tr><td colspan="5" class="empty-state">Joined challenges could not be loaded.</td></tr>';
      showNotice(error.message, 'error');
    }
  }

  async function loadLeaderboard(month) {
    if (!month) return;
    leaderboardTable.innerHTML = '<tr><td colspan="6" class="empty-state">Loading leaderboard…</td></tr>';
    try {
      const rows = await api(`/api/leaderboard?month=${encodeURIComponent(month)}`);
      leaderboardTable.innerHTML = rows.length ? rows.map((row) => {
        const reduction = Number(row.reductionPercentage || 0);
        const progress = Math.max(0, Math.min(100, reduction));
        return `<tr><td class="rank-cell"><span class="rank-badge ${Number(row.rank) === 1 ? 'rank-first' : ''}">${escapeHtml(row.rank)}</span></td><td><strong>${escapeHtml(row.householdName)}</strong></td>
          <td>${formatNumber(row.previousMonthFootprintKg)} kg</td><td>${formatNumber(row.currentMonthFootprintKg)} kg</td>
          <td><span class="reduction-badge ${reduction < 0 ? 'negative' : ''}">${formatNumber(reduction)}%</span></td>
          <td><div class="progress-track" role="progressbar" aria-label="Reduction percentage" aria-valuemin="0" aria-valuemax="100" aria-valuenow="${Math.round(progress)}"><span style="width:${progress}%"></span></div></td></tr>`;
      }).join('') : '<tr><td colspan="6" class="empty-state">No leaderboard results for this month.</td></tr>';
    } catch (error) {
      leaderboardTable.innerHTML = '<tr><td colspan="6" class="empty-state">The leaderboard could not be loaded.</td></tr>';
      showNotice(error.message, 'error');
    }
  }

  function formPayload(form) {
    return Object.fromEntries(new FormData(form).entries());
  }

  function setSubmitting(form, submitting) {
    const button = form.querySelector('button[type="submit"]');
    button.disabled = submitting;
    button.dataset.originalText ||= button.textContent;
    button.textContent = submitting ? 'Working…' : button.dataset.originalText;
  }

  byId('household-form').addEventListener('submit', async (event) => {
    event.preventDefault();
    const form = event.currentTarget;
    const payload = formPayload(form);
    payload.membersCount = Number(payload.membersCount);
    setSubmitting(form, true);
    try {
      await api('/api/households', { method: 'POST', body: JSON.stringify(payload) });
      form.reset();
      await loadHouseholds();
      showNotice('Household created successfully.');
    } catch (error) { showNotice(error.message, 'error'); }
    finally { setSubmitting(form, false); }
  });

  byId('activity-form').addEventListener('submit', async (event) => {
    event.preventDefault();
    const form = event.currentTarget;
    const payload = formPayload(form);
    const householdId = payload.householdId;
    delete payload.householdId;
    payload.quantity = Number(payload.quantity);
    setSubmitting(form, true);
    try {
      await api(`/api/households/${encodeURIComponent(householdId)}/activities`, { method: 'POST', body: JSON.stringify(payload) });
      await loadActivities(householdId);
      await loadDashboardData();
      showNotice('Activity recorded successfully.');
      form.elements.quantity.value = '';
    } catch (error) { showNotice(error.message, 'error'); }
    finally { setSubmitting(form, false); }
  });

  byId('challenge-form').addEventListener('submit', async (event) => {
    event.preventDefault();
    const form = event.currentTarget;
    const payload = formPayload(form);
    payload.targetReductionPercentage = Number(payload.targetReductionPercentage);
    setSubmitting(form, true);
    try {
      await api('/api/challenges', { method: 'POST', body: JSON.stringify(payload) });
      form.reset();
      await loadChallenges();
      showNotice('Challenge created successfully.');
    } catch (error) { showNotice(error.message, 'error'); }
    finally { setSubmitting(form, false); }
  });

  byId('footprint-form').addEventListener('submit', async (event) => {
    event.preventDefault();
    const { householdId, month } = formPayload(event.currentTarget);
    const result = byId('footprint-result');
    result.innerHTML = '<span class="result-label">TOTAL EMISSIONS</span><strong>… <small>kg CO₂e</small></strong><p>Loading household report.</p>';
    try {
      const footprint = await api(`/api/households/${encodeURIComponent(householdId)}/footprint?month=${encodeURIComponent(month)}`);
      const household = households.find((item) => String(item.id) === String(footprint.householdId));
      result.innerHTML = `<span class="result-label">${escapeHtml(footprint.month)} · TOTAL EMISSIONS</span><strong>${formatNumber(footprint.totalCarbonEmissionKg)} <small>kg CO₂e</small></strong><p>${escapeHtml(household?.name || `Household ${footprint.householdId}`)} monthly footprint</p>`;
      showNotice('Monthly footprint loaded.');
    } catch (error) {
      result.innerHTML = '<span class="result-label">TOTAL EMISSIONS</span><strong>— <small>kg CO₂e</small></strong><p>The report could not be loaded.</p>';
      showNotice(error.message, 'error');
    }
  });

  byId('leaderboard-form').addEventListener('submit', (event) => {
    event.preventDefault();
    loadLeaderboard(formPayload(event.currentTarget).month);
  });

  document.addEventListener('change', (event) => {
    if (event.target.matches('#activity-form [name="householdId"]')) loadActivities(event.target.value);
    if (event.target.matches('[data-participation-select]')) loadParticipations(event.target.value);
    if (event.target.matches('#activity-form [name="activityType"]')) {
      byId('quantity-unit').textContent = ({ CAR_COMMUTE: 'km', BUS_COMMUTE: 'km', ELECTRICITY: 'kWh', WASTE: 'kg' })[event.target.value] || 'Choose an activity type';
    }
  });

  document.addEventListener('click', async (event) => {
    const joinButton = event.target.closest('.join-button');
    if (joinButton) {
      const card = joinButton.closest('.challenge-card');
      const householdId = card.querySelector('.challenge-household-select').value;
      if (!householdId) { showNotice('Select a household before joining this challenge.', 'error'); return; }
      joinButton.disabled = true;
      try {
        await api(`/api/challenges/${encodeURIComponent(joinButton.dataset.challengeId)}/join/${encodeURIComponent(householdId)}`, { method: 'POST' });
        const selected = document.querySelector('[data-participation-select]');
        selected.value = householdId;
        await loadParticipations(householdId);
        showNotice('Household joined the challenge.');
      } catch (error) { showNotice(error.message, 'error'); }
      finally { joinButton.disabled = false; }
      return;
    }
    const refreshButton = event.target.closest('[data-refresh]');
    if (refreshButton?.dataset.refresh === 'households') await loadHouseholds();
    if (refreshButton?.dataset.refresh === 'challenges') await loadChallenges();
    if (refreshButton?.dataset.refresh === 'activities') {
      await loadActivities(document.querySelector('#activity-form [name="householdId"]').value);
    }
  });

  document.querySelectorAll('.main-nav a').forEach((link) => link.addEventListener('click', () => {
    document.querySelectorAll('.main-nav a').forEach((item) => item.classList.toggle('active', item === link));
  }));
  byId('reporting-month').textContent = monthNow;
  Promise.all([loadHouseholds(), loadChallenges()]);
})();