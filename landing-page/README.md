# IREN — Landing Page & Interactive Web Showcase

Official web landing page and interactive hardware/cloud simulator for **IREN** (Image Recognition & Explanation Network / HybridMind), the hybrid on-device and cloud AI assistant for Android.

## 🚀 Live Deployment Options

### Deploy to Vercel (Recommended)
1. Push this folder to GitHub or link the repository on [vercel.com](https://vercel.com).
2. Set Root Directory to `landing-page` (if deploying from the HybridMind monorepo), or import directly.
3. Framework Preset: **Vite**.
4. Build Command: `npm run build`
5. Output Directory: `dist`
6. Deploy! URL will be available immediately (e.g., `https://iren-ai.vercel.app`).

### Deploy to Netlify
1. Netlify configuration is already included via `netlify.toml`.
2. Connect your repository on [netlify.com](https://netlify.com) or run `npx netlify deploy --prod --dir=dist`.

## 💻 Local Development

```bash
# Install dependencies
npm install

# Start local development server (with hot reload)
npm run dev

# Build production bundle
npm run build

# Preview production build locally
npm run preview
```

## ✨ Features Included on Landing Page
- **Interactive Live Simulator**: Real-time switchable network environment ([Online: Gemini 2.5 Flash] vs [Offline: Gemma 3n LiteRT-LM]) with streaming responses, latency timers, and privacy badges.
- **Dynamic Routing Pipeline**: Visual architectural breakdown of `ChatRepository.kt`, `NetworkMonitor.kt`, and `AutoPruneWorker.kt`.
- **Hardware Benchmarks Matrix**: Full breakdown of Gemini 2.5 Flash vs Gemma 3n E4B vs Gemma 3n E2B vs Gemma 2B.
- **Production Kotlin Showcase**: Interactive tabbed code viewer displaying real, production-ready Android code.
- **Glassmorphic Jetpack Compose Theme**: Styled with the app's native dark obsidian (`#070A1E`), cyan neon (`#00D9FF`), and ethereal purple glows.

