import express from "express";
import cors from "cors";
import { v4 as uuid } from "uuid";
import { WebSocketServer } from "ws";

const app = express();
const PORT = process.env.PORT || 3000;

app.use(express.json());
app.use(cors());

// In-memory data for demo
const users = new Map(); // email -> user record
const channels = new Map([
  ["general", new Set()],
  ["random", new Set()],
  ["support", new Set()],
]);

function buildToken(email) {
  return Buffer.from(`${email}:${Date.now()}`).toString("base64");
}

function publicUser(user) {
  const { password, ...rest } = user;
  return rest;
}

app.post("/register", (req, res) => {
  const { first_name, last_name, email, password, confirm_password } = req.body || {};
  if (!email || !password || !confirm_password) {
    return res.status(400).json({ message: "Datos invalidos o incompletos" });
  }
  if (password !== confirm_password) {
    return res.status(400).json({ message: "Las contrasenas no coinciden" });
  }
  if (users.has(email)) {
    return res.status(409).json({ message: "El usuario ya existe" });
  }
  const user = {
    user_id: users.size + 1,
    email,
    first_name: first_name || "",
    last_name: last_name || "",
    password,
    token: buildToken(email),
  };
  users.set(email, user);
  res.json({ message: "Registro exitoso" });
});

app.post("/login", (req, res) => {
  const { email, password } = req.body || {};
  const user = users.get(email);
  if (!user || user.password !== password) {
    return res.status(401).json({ message: "Usuario o contrasena incorrectos" });
  }
  // issue new token
  user.token = buildToken(email);
  res.json(publicUser(user));
});

app.post("/logout", (_req, res) => {
  res.json({ message: "Logout exitoso" });
});

app.get("/channels", (_req, res) => {
  res.json(Array.from(channels.keys()));
});

app.get("/channel-users", (req, res) => {
  const canal = req.query.canal || "general";
  const set = channels.get(canal);
  if (!set) {
    return res.status(404).json({ message: "Canal no encontrado" });
  }
  res.json(Array.from(set));
});

const server = app.listen(PORT, () => {
  console.log(`API listening on http://0.0.0.0:${PORT}`);
});

// Simple WebSocket to echo audio messages
const wss = new WebSocketServer({ server, path: "/ws" });

wss.on("connection", (ws, req) => {
  const url = new URL(req.url || "", `http://${req.headers.host}`);
  const channel = url.searchParams.get("channel") || "general";
  const userId = url.searchParams.get("userId") || "guest";

  if (!channels.has(channel)) {
    channels.set(channel, new Set());
  }
  channels.get(channel).add(`user-${userId}`);

  ws.on("message", (data) => {
    // Broadcast to all in the same channel
    wss.clients.forEach((client) => {
      if (client !== ws && client.readyState === ws.OPEN) {
        client.send(data);
      }
    });
  });

  ws.on("close", () => {
    const set = channels.get(channel);
    if (set) {
      set.delete(`user-${userId}`);
    }
  });
});
