const config = window.DURBIN_FIREBASE_CONFIG;
firebase.initializeApp(config);

const auth = firebase.auth();
const db = firebase.database();
const provider = new firebase.auth.GoogleAuthProvider();

const $ = (id) => document.getElementById(id);

// Simple dashboard admin lock.
// Note: static Netlify apps cannot hide client-side passwords from advanced users.
// Keep Firebase admin UID rules enabled for real protection.
const DURBIN_ADMINS = {
  COSA: "catslikecosa",
  MOD: "catslikecosa"
};

function setAdminLocked(locked) {
  document.body.classList.toggle("admin-locked", locked);
  const gate = $("adminGate");
  if (gate) gate.classList.toggle("hidden", !locked);
}

function unlockAdmin(username) {
  localStorage.setItem("durbinAdminUser", username);
  const badge = $("adminNameBadge");
  if (badge) {
    badge.textContent = `Admin: ${username}`;
    badge.classList.remove("hidden");
  }
  setAdminLocked(false);
}

function bootAdminGate() {
  const saved = localStorage.getItem("durbinAdminUser");
  if (saved && DURBIN_ADMINS[saved]) {
    unlockAdmin(saved);
    return;
  }

  setAdminLocked(true);

  const btn = $("adminLoginBtn");
  if (!btn) return;

  btn.addEventListener("click", () => {
    const username = ($("adminUsername").value || "").trim().toUpperCase();
    const password = $("adminPassword").value || "";
    const error = $("adminLoginError");

    if (DURBIN_ADMINS[username] && DURBIN_ADMINS[username] === password) {
      if (error) error.textContent = "";
      unlockAdmin(username);
      toast(`Welcome ${username}`);
    } else {
      if (error) error.textContent = "Wrong username or password.";
    }
  });

  const pass = $("adminPassword");
  if (pass) {
    pass.addEventListener("keydown", (e) => {
      if (e.key === "Enter") btn.click();
    });
  }
}

function toast(msg) {
  const el = $("toast");
  el.textContent = msg;
  el.classList.add("show");
  setTimeout(() => el.classList.remove("show"), 3200);
}

function withTimeout(promise, ms = 12000) {
  return Promise.race([
    promise,
    new Promise((_, reject) => {
      setTimeout(() => reject(new Error("Firebase request timed out. Check rules, internet, and database URL.")), ms);
    })
  ]);
}

function showLoadError(box, path, error) {
  console.error("Firebase error:", path, error);
  const msg = error && error.message ? error.message : String(error || "Unknown error");
  const code = error && error.code ? error.code : "";
  box.innerHTML = `
    <div class="item error-item">
      <h4>Could not load data</h4>
      <p><b>Path:</b> <code>${escapeHtml(path)}</code></p>
      ${code ? `<p><b>Code:</b> ${escapeHtml(code)}</p>` : ""}
      <p>${escapeHtml(msg)}</p>
      <p class="mini">Fix: add your UID as admin and paste the included Firebase rules.</p>
    </div>
  `;
  toast(msg);
}

function slugify(input) {
  return String(input || "")
    .trim()
    .toLowerCase()
    .replace(/[^a-z0-9_-]+/g, "_")
    .replace(/^_+|_+$/g, "") || `id_${Date.now()}`;
}

function nowMs() {
  return Date.now();
}

function emailKey(email) {
  return String(email || "")
    .trim()
    .toLowerCase()
    .replaceAll(".", "_dot_")
    .replaceAll("@", "_at_")
    .replaceAll("#", "_")
    .replaceAll("$", "_")
    .replaceAll("[", "_")
    .replaceAll("]", "_")
    .replaceAll("/", "_");
}

function requireUser() {
  const user = auth.currentUser;
  if (!user) {
    toast("Sign in with Google first.");
    throw new Error("Not signed in");
  }
  return user;
}

function escapeHtml(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

document.querySelectorAll(".nav").forEach(btn => {
  btn.addEventListener("click", () => {
    document.querySelectorAll(".nav").forEach(b => b.classList.remove("active"));
    document.querySelectorAll(".panel").forEach(p => p.classList.remove("active"));
    btn.classList.add("active");
    $(btn.dataset.panel).classList.add("active");
  });
});

$("loginBtn").addEventListener("click", async () => {
  try {
    await auth.signInWithPopup(provider);
  } catch (e) {
    console.error(e);
    toast(e.message || "Login failed");
  }
});

$("signOutBtn").addEventListener("click", () => auth.signOut());

$("copyUidBtn").addEventListener("click", async () => {
  const user = auth.currentUser;
  if (!user) return;
  await navigator.clipboard.writeText(user.uid);
  toast("UID copied.");
});

auth.onAuthStateChanged(user => {
  if (user) {
    $("loginBtn").classList.add("hidden");
    $("userBox").classList.remove("hidden");
    $("userName").textContent = `${user.displayName || user.email} • UID: ${user.uid}`;
    if ($("rankUid")) $("rankUid").value = user.uid;
    if ($("rankEmail")) $("rankEmail").value = user.email || "";
    $("notice").innerHTML = `Signed in. UID: <code>${user.uid}</code>. If saving fails, add this UID as admin in Firebase.`;
    loadNews();
    loadTier();
    loadAd();
  } else {
    $("loginBtn").classList.remove("hidden");
    $("userBox").classList.add("hidden");
    $("notice").textContent = "Sign in first. Your UID will show here so you can add yourself as admin in Firebase.";
  }
});

$("newsForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  try {
    requireUser();

    const id = slugify($("newsId").value || `news_${Date.now()}`);
    const data = {
      title: $("newsTitle").value.trim(),
      body: $("newsBody").value.trim(),
      tag: $("newsTag").value.trim() || "News",
      imageUrl: $("newsImageUrl").value.trim(),
      linkUrl: $("newsLinkUrl").value.trim(),
      pinned: $("newsPinned").checked,
      timestamp: nowMs(),
      updatedBy: auth.currentUser.uid
    };

    await withTimeout(db.ref(`durbin/news/${id}`).set(data));
    $("newsForm").reset();
    $("newsTag").value = "Update";
    toast("News saved.");
    loadNews();
  } catch (error) {
    toast(error.message || "News save failed.");
  }
});

$("refreshNewsBtn").addEventListener("click", loadNews);

async function loadNews() {
  const box = $("newsList");
  const path = "durbin/news";
  box.innerHTML = `<div class="item"><p>Loading news...</p><p class="mini">Max wait: 12 seconds.</p></div>`;

  try {
    const snap = await withTimeout(db.ref(path).orderByChild("timestamp").limitToLast(30).get());
    if (!snap.exists()) {
      box.innerHTML = `<div class="item"><p>No news yet.</p><p class="mini">Add your first news using the form above.</p></div>`;
      return;
    }

    const items = [];
    snap.forEach(child => items.push({ id: child.key, ...child.val() }));
    items.sort((a, b) => (b.timestamp || 0) - (a.timestamp || 0));

    box.innerHTML = items.map(n => `
      <div class="item">
        <div class="row">
          <div>
            <span class="tag">${escapeHtml(n.tag || "News")}</span>
            <h4>${escapeHtml(n.title || n.id)}</h4>
          </div>
          <span class="mini">${n.pinned ? "PINNED" : ""}</span>
        </div>
        <p>${escapeHtml(n.body || "")}</p>
        ${n.imageUrl ? `<p class="mini">Image: ${escapeHtml(n.imageUrl)}</p>` : ""}
        <div class="item-actions">
          <button class="ghost" onclick="editNews('${n.id}')">Edit</button>
          <button class="danger" onclick="deleteNews('${n.id}')">Delete</button>
        </div>
      </div>
    `).join("");
  } catch (error) {
    showLoadError(box, path, error);
  }
}

window.editNews = async (id) => {
  try {
    const snap = await withTimeout(db.ref(`durbin/news/${id}`).get());
    if (!snap.exists()) return toast("News not found.");
    const n = snap.val();
    $("newsId").value = id;
    $("newsTitle").value = n.title || "";
    $("newsTag").value = n.tag || "News";
    $("newsImageUrl").value = n.imageUrl || "";
    $("newsLinkUrl").value = n.linkUrl || "";
    $("newsBody").value = n.body || "";
    $("newsPinned").checked = !!n.pinned;
    toast("Loaded news into form. Save again to update.");
  } catch (error) {
    toast(error.message || "Could not load news.");
  }
};

window.deleteNews = async (id) => {
  try {
    requireUser();
    if (!confirm(`Delete news ${id}?`)) return;
    await withTimeout(db.ref(`durbin/news/${id}`).remove());
    toast("News deleted.");
    loadNews();
  } catch (error) {
    toast(error.message || "News delete failed.");
  }
};

$("tierForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  try {
    requireUser();

    const category = $("tierCategory").value;
    const ign = $("tierIgn").value.trim();
    const entryId = slugify(ign);

    await withTimeout(db.ref(`durbin/pvpTierLists/${category}`).update({
      name: category,
      description: `${category} leaderboard`,
      updatedAt: nowMs()
    }));

    await withTimeout(db.ref(`durbin/pvpTierLists/${category}/entries/${entryId}`).set({
      uid: entryId,
      ign,
      tier: $("tierValue").value,
      score: Number($("tierScore").value || 0),
      region: $("tierRegion").value.trim(),
      country: $("tierCountry").value.trim(),
      notes: $("tierNotes").value.trim(),
      verified: $("tierVerified").checked,
      updatedAt: nowMs(),
      updatedBy: auth.currentUser.uid
    }));

    toast("Tier entry saved.");
    loadTier();
  } catch (error) {
    toast(error.message || "Tier save failed.");
  }
});

$("tierCategory").addEventListener("change", loadTier);
$("refreshTierBtn").addEventListener("click", loadTier);

async function loadTier() {
  const category = $("tierCategory").value;
  const box = $("tierList");
  const path = `durbin/pvpTierLists/${category}/entries`;
  box.innerHTML = `<div class="item"><p>Loading ${escapeHtml(category)}...</p><p class="mini">Max wait: 12 seconds.</p></div>`;

  try {
    const snap = await withTimeout(db.ref(path).get());
    if (!snap.exists()) {
      box.innerHTML = `<div class="item"><p>No entries in ${escapeHtml(category)} yet.</p><p class="mini">Add one using the form above.</p></div>`;
      return;
    }

    const items = [];
    snap.forEach(child => items.push({ id: child.key, ...child.val() }));
    items.sort((a, b) => (b.score || 0) - (a.score || 0));

    box.innerHTML = items.map(p => `
      <div class="item">
        <div class="row">
          <div>
            <span class="tag">${escapeHtml(p.tier || "UNRANKED")}</span>
            <h4>${escapeHtml(p.ign || p.id)}</h4>
          </div>
          <span class="mini">${p.score || 0} pts • ${escapeHtml(p.region || "")}</span>
        </div>
        <p>${escapeHtml(p.country || "")} ${p.verified ? "• Verified" : ""}</p>
        ${p.notes ? `<p>${escapeHtml(p.notes)}</p>` : ""}
        <div class="item-actions">
          <button class="ghost" onclick="editTier('${category}','${p.id}')">Edit</button>
          <button class="danger" onclick="deleteTier('${category}','${p.id}')">Delete</button>
        </div>
      </div>
    `).join("");
  } catch (error) {
    showLoadError(box, path, error);
  }
}

window.editTier = async (category, id) => {
  try {
    const snap = await withTimeout(db.ref(`durbin/pvpTierLists/${category}/entries/${id}`).get());
    if (!snap.exists()) return toast("Tier entry not found.");
    const p = snap.val();
    $("tierCategory").value = category;
    $("tierIgn").value = p.ign || id;
    $("tierValue").value = p.tier || "HT3";
    $("tierScore").value = p.score || 0;
    $("tierRegion").value = p.region || "AS";
    $("tierCountry").value = p.country || "";
    $("tierNotes").value = p.notes || "";
    $("tierVerified").checked = !!p.verified;
    toast("Loaded tier entry into form. Save again to update.");
  } catch (error) {
    toast(error.message || "Could not load tier entry.");
  }
};

window.deleteTier = async (category, id) => {
  try {
    requireUser();
    if (!confirm(`Delete ${id} from ${category}?`)) return;
    await withTimeout(db.ref(`durbin/pvpTierLists/${category}/entries/${id}`).remove());
    toast("Tier entry deleted.");
    loadTier();
  } catch (error) {
    toast(error.message || "Tier delete failed.");
  }
};

$("rankForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  try {
    requireUser();

    const email = $("rankEmail").value.trim().toLowerCase();
    const uid = $("rankUid").value.trim();
    const category = $("rankCategory").value;
    const key = emailKey(email);

    const rankData = {
      categoryName: category,
      email,
      ign: $("rankIgn").value.trim(),
      tier: $("rankTier").value,
      score: Number($("rankScore").value || 0),
      region: $("rankRegion").value.trim(),
      updatedAt: nowMs(),
      updatedBy: auth.currentUser.uid
    };

    await withTimeout(db.ref(`durbin/userRanksByEmail/${key}/email`).set(email));
    await withTimeout(db.ref(`durbin/userRanksByEmail/${key}/ranks/${category}`).set(rankData));

    if (uid) {
      await withTimeout(db.ref(`durbin/userRanks/${uid}/email`).set(email));
      await withTimeout(db.ref(`durbin/userRanks/${uid}/ranks/${category}`).set(rankData));
    }

    toast("Gmail rank saved.");
  } catch (error) {
    toast(error.message || "Rank save failed.");
  }
});


const refreshAdButton = $("refreshAdBtn");
if (refreshAdButton) refreshAdButton.addEventListener("click", loadAd);

const adForm = $("adForm");
if (adForm) {
  adForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    try {
      requireUser();

      const data = {
        imageUrl: $("adImageUrl").value.trim(),
        linkUrl: $("adLinkUrl").value.trim(),
        type: $("adType").value,
        alt: $("adAlt").value.trim() || "DURBIN Ad",
        enabled: $("adEnabled").checked,
        aspectRatio: "9:16",
        updatedAt: nowMs(),
        updatedBy: auth.currentUser.uid
      };

      await withTimeout(db.ref("durbin/ads/main").set(data));
      toast("Ad saved.");
      loadAd();
    } catch (error) {
      toast(error.message || "Ad save failed.");
    }
  });
}

async function loadAd() {
  const box = $("adPreview");
  if (!box) return;

  const path = "durbin/ads/main";
  box.innerHTML = `<div class="item"><p>Loading ad...</p><p class="mini">Max wait: 12 seconds.</p></div>`;

  try {
    const snap = await withTimeout(db.ref(path).get());
    if (!snap.exists()) {
      box.innerHTML = `<div class="item"><p>No ad added yet.</p><p class="mini">Paste an image URL and click Save Ad.</p></div>`;
      return;
    }

    const ad = snap.val();
    $("adImageUrl").value = ad.imageUrl || "";
    $("adLinkUrl").value = ad.linkUrl || "";
    $("adType").value = ad.type || "image";
    $("adAlt").value = ad.alt || "DURBIN Ad";
    $("adEnabled").checked = !!ad.enabled;

    if (!ad.imageUrl) {
      box.innerHTML = `<div class="item"><p>Ad exists but imageUrl is empty.</p></div>`;
      return;
    }

    box.innerHTML = `
      <div class="ad-preview-card">
        <img src="${escapeHtml(ad.imageUrl)}" alt="${escapeHtml(ad.alt || "DURBIN Ad")}" />
      </div>
      <div class="item">
        <p><b>Status:</b> ${ad.enabled ? "Enabled" : "Disabled"}</p>
        <p><b>Type:</b> ${escapeHtml(ad.type || "image")}</p>
        ${ad.linkUrl ? `<p><b>Link:</b> ${escapeHtml(ad.linkUrl)}</p>` : ""}
        <div class="item-actions">
          <button class="danger" onclick="deleteAd()">Delete Ad</button>
        </div>
      </div>
    `;
  } catch (error) {
    showLoadError(box, path, error);
  }
}

window.deleteAd = async () => {
  try {
    requireUser();
    if (!confirm("Delete current ad?")) return;
    await withTimeout(db.ref("durbin/ads/main").remove());
    toast("Ad deleted.");
    loadAd();
  } catch (error) {
    toast(error.message || "Ad delete failed.");
  }
};


bootAdminGate();
