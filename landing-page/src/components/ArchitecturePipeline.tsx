import React, { useState } from 'react';
import { Network, RefreshCw, Cpu, HardDrive, CheckCircle2, Zap } from 'lucide-react';

export const ArchitecturePipeline: React.FC = () => {
  const [activeStep, setActiveStep] = useState<number>(1);

  const steps = [
    {
      id: 1,
      title: "Input & Context Ingestion",
      icon: Network,
      tag: "Sensory Layer",
      summary: "Captures user query, voice STT, or camera image into memory.",
      details: [
        "Multimodal Image Preprocessing: Bitmaps normalized to 224x224 RGB tensors or compressed to byte arrays.",
        "Voice Input: High-accuracy real-time speech-to-text dictation.",
        "Session Context Extraction: Reconstructs previous turn history while sanitizing system messages."
      ],
      codeRef: "ChatRepository.kt:118"
    },
    {
      id: 2,
      title: "Reactive Network Monitor & Router",
      icon: RefreshCw,
      tag: "Dynamic Fallback",
      summary: "Evaluates connectivity via ConnectivityManager Coroutines Flow.",
      details: [
        "Continuous Network Stream: StateFlow<Boolean> tracks WiFi, Cellular, and Metered states with zero polling overhead.",
        "Automatic Fault Interceptor: If Gemini cloud call times out or throws IOException, catches the exception and immediately switches to LiteRT-LM.",
        "Session Tagging: Tags session 'is_offline_only = true' if local fallback is engaged."
      ],
      codeRef: "NetworkMonitor.kt:18"
    },
    {
      id: 3,
      title: "Dual Compute Inference Engine",
      icon: Cpu,
      tag: "Edge vs Cloud",
      summary: "Executes generation on either Google TPU cloud or Mobile GPU.",
      details: [
        "Cloud: Gemini 2.5 Flash streaming with system instructions and high-level reasoning.",
        "Edge: LiteRT-LM (formerly MediaPipe LLM Inference) running Gemma 3n E2B (2GB) or E4B (4GB) with OpenCL GPU shaders.",
        "Memory Guardian: Validates available device RAM before model initialization to prevent Out-Of-Memory (OOM) kill."
      ],
      codeRef: "ChatRepository.kt:50-109"
    },
    {
      id: 4,
      title: "Zero-Knowledge Storage & Lifecycle",
      icon: HardDrive,
      tag: "Privacy & Retention",
      summary: "Isolates private queries and manages device storage automatically.",
      details: [
        "Selective Cloud Sync: Only online messages sync to Firebase Firestore; offline messages are physically blocked from syncing.",
        "Local SQLite Room: Persists sessions with indexed foreign keys and rapid query retrieval.",
        "WorkManager Auto-Pruner: Background AutoPruneWorker runs periodic housekeeping, purging offline chats older than 90 days."
      ],
      codeRef: "AutoPruneWorker.kt:22"
    }
  ];

  return (
    <section id="architecture" className="py-24 relative overflow-hidden bg-[#EAE7DF] border-t border-[#DED9CE]">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        {/* Section Header */}
        <div className="text-center max-w-3xl mx-auto mb-16">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-[#F7F5EF] border border-[#DED9CE] text-[#424A43] text-xs font-mono mb-4 shadow-subtle">
            <Zap className="w-3.5 h-3.5 text-[#B69A75]" />
            <span>Under The Hood</span>
          </div>
          <h2 className="text-3xl sm:text-5xl font-serif font-normal text-[#171B19] tracking-tight">
            Hybrid Routing Architecture
          </h2>
          <p className="mt-4 text-[#3A403C] text-base sm:text-lg leading-relaxed">
            How IREN orchestrates cloud scale with zero-latency edge privacy in a unified Android lifecycle.
          </p>
        </div>

        {/* Visual Pipeline 4-Stage Cards */}
        <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
          {steps.map((step) => {
            const Icon = step.icon;
            const isSelected = activeStep === step.id;

            return (
              <div
                key={step.id}
                onClick={() => setActiveStep(step.id)}
                className={`cursor-pointer p-6 rounded-xl transition-all duration-200 border flex flex-col justify-between ${
                  isSelected
                    ? 'bg-[#F7F5EF] border-[#879487] shadow-card'
                    : 'bg-[#EAE7DF] border-[#DED9CE] hover:border-[#879487]/50 hover:bg-[#F1EFE8]'
                }`}
              >
                <div>
                  <div className="flex items-center justify-between mb-4">
                    <div className={`w-10 h-10 rounded-lg flex items-center justify-center transition-colors ${
                      isSelected ? 'bg-[#171B19] text-[#F7F5EF]' : 'bg-[#F7F5EF] text-[#575E59] border border-[#DED9CE]'
                    }`}>
                      <Icon className="w-5 h-5" />
                    </div>
                    <span className="text-[10px] font-mono font-medium px-2 py-0.5 rounded bg-[#DED9CE]/60 text-[#424A43]">
                      STEP 0{step.id}
                    </span>
                  </div>

                  <h3 className="text-base font-serif font-normal text-[#171B19] mb-1.5 leading-snug">
                    {step.title}
                  </h3>
                  <p className="text-xs text-[#575E59] line-clamp-2 leading-relaxed">
                    {step.summary}
                  </p>
                </div>

                <div className="mt-5 pt-3 border-t border-[#DED9CE] flex items-center justify-between text-[11px] font-mono">
                  <span className={isSelected ? 'text-[#3C4A3C] font-semibold' : 'text-[#717872]'}>{step.tag}</span>
                  <span className="text-[#879487] font-sans font-medium">Inspect →</span>
                </div>
              </div>
            );
          })}
        </div>

        {/* Active Step Deep-Dive Card */}
        <div className="bg-[#F7F5EF] p-6 sm:p-8 rounded-xl border border-[#DED9CE] shadow-subtle">
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 pb-6 border-b border-[#DED9CE]">
            <div>
              <div className="flex items-center gap-3">
                <span className="text-[10px] font-mono px-2.5 py-1 rounded bg-[#879487]/15 text-[#3C4A3C] font-semibold uppercase tracking-wider">
                  STAGE {activeStep} DEEP-DIVE
                </span>
                <span className="text-xs font-mono text-[#717872]">
                  File: {steps[activeStep - 1].codeRef}
                </span>
              </div>
              <h3 className="text-2xl font-serif font-normal text-[#171B19] mt-2">
                {steps[activeStep - 1].title}
              </h3>
            </div>

            <div className="text-xs font-mono text-[#575E59] px-3 py-1.5 rounded bg-[#EAE7DF] border border-[#DED9CE]">
              Module: <strong className="text-[#171B19]">com.example.hybridmind</strong>
            </div>
          </div>

          <div className="mt-6 grid md:grid-cols-3 gap-5">
            {steps[activeStep - 1].details.map((detail, dIdx) => (
              <div key={dIdx} className="p-4 rounded-lg bg-[#EAE7DF] border border-[#DED9CE]">
                <div className="flex items-start gap-2.5">
                  <CheckCircle2 className="w-4 h-4 text-[#879487] shrink-0 mt-0.5" />
                  <p className="text-xs sm:text-[13px] text-[#2C312E] leading-relaxed font-sans">
                    {detail}
                  </p>
                </div>
              </div>
            ))}
          </div>
        </div>

      </div>
    </section>
  );
};
