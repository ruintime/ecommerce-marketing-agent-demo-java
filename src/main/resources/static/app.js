const messages = document.querySelector("#messages");
const form = document.querySelector("#chatForm");
const input = document.querySelector("#questionInput");
const planList = document.querySelector("#planList");
const evidenceList = document.querySelector("#evidenceList");
const toolCallList = document.querySelector("#toolCallList");
const pendingAction = document.querySelector("#pendingAction");
const loadToolsButton = document.querySelector("#loadToolsButton");

form.addEventListener("submit", async (event) => {
  event.preventDefault();
  const question = input.value.trim();
  if (!question) return;
  appendMessage("user", question);
  input.value = "";
  appendMessage("agent", "处理中：检索知识库并调用业务工具...");

  const response = await fetch("/api/agent/chat", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ question, operatorId: "demo-operator" })
  });
  const data = await response.json();
  messages.lastElementChild.remove();
  appendMessage("agent", data.answer);
  renderInspector(data);
});

loadToolsButton.addEventListener("click", async () => {
  const response = await fetch("/api/mcp/tools");
  const tools = await response.json();
  toolCallList.innerHTML = tools.map(tool => `
    <article class="item">
      <strong>${tool.name} <span class="muted">(${tool.riskLevel})</span></strong>
      <p>${tool.description}</p>
      <pre>${escapeHtml(JSON.stringify(tool.inputSchema, null, 2))}</pre>
    </article>
  `).join("");
});

function appendMessage(role, text) {
  const article = document.createElement("article");
  article.className = `message ${role}`;
  article.innerHTML = `
    <span class="avatar">${role === "user" ? "你" : "AI"}</span>
    <div class="bubble"><p>${escapeHtml(text)}</p></div>
  `;
  messages.appendChild(article);
  messages.scrollTop = messages.scrollHeight;
}

function renderInspector(data) {
  planList.innerHTML = data.plan.map(step => `<li>${escapeHtml(step)}</li>`).join("");
  evidenceList.innerHTML = data.evidence.map(chunk => `
    <article class="item">
      <strong>${escapeHtml(chunk.title)}</strong>
      <p>${escapeHtml(chunk.content)}</p>
    </article>
  `).join("");
  toolCallList.innerHTML = data.toolCalls.map(call => `
    <article class="item">
      <strong>${escapeHtml(call.toolName)} <span class="muted">${call.latencyMs}ms</span></strong>
      <p>参数</p>
      <pre>${escapeHtml(JSON.stringify(call.arguments, null, 2))}</pre>
      <p>结果</p>
      <pre>${escapeHtml(JSON.stringify(call.result, null, 2))}</pre>
    </article>
  `).join("");

  if (!data.pendingAction) {
    pendingAction.className = "stack muted";
    pendingAction.textContent = "暂无";
    return;
  }
  pendingAction.className = "stack";
  pendingAction.innerHTML = `
    <article class="item">
      <strong>${escapeHtml(data.pendingAction.actionType)} <span class="muted">${escapeHtml(data.pendingAction.status)}</span></strong>
      <pre>${escapeHtml(JSON.stringify(data.pendingAction.payload, null, 2))}</pre>
      <div class="approve-row">
        <button type="button" data-approve="${data.pendingAction.actionId}">审批通过</button>
        <button class="reject" type="button" data-reject="${data.pendingAction.actionId}">拒绝</button>
      </div>
    </article>
  `;
  pendingAction.querySelector("[data-approve]").addEventListener("click", () => decide(data.pendingAction.actionId, true));
  pendingAction.querySelector("[data-reject]").addEventListener("click", () => decide(data.pendingAction.actionId, false));
}

async function decide(actionId, approved) {
  const response = await fetch(`/api/agent/approve/${actionId}`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ approved, reviewer: "demo-reviewer", comment: "Demo review" })
  });
  const result = await response.json();
  pendingAction.innerHTML = `
    <article class="item">
      <strong>${escapeHtml(result.status)}</strong>
      <p>${escapeHtml(result.message)}</p>
    </article>
  `;
}

function escapeHtml(value) {
  return String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}
