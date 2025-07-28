import jwt from "jsonwebtoken";
import { TOKEN_SECRET } from "../config.js";

export const authRequiered = (req, res, next) => {
  // Buscar token en headers Authorization, cookies, o body
  let token =
    req.headers.authorization?.split(" ")[1] || // Bearer token
    req.cookies.token || // Cookie
    req.body.token; // Body

  if (!token)
    return res.status(401).json({ message: "No token, authorization denied" });

  jwt.verify(token, TOKEN_SECRET, (err, user) => {
    if (err) return res.status(403).json({ message: "Invalid token" });

    req.user = user;
    req.userId = user.id;
    next();
  });
};
