import React from 'react';
import { Layers, ShieldCheck } from 'lucide-react';

export const TechStackSection: React.FC = () => {
  const stack = [
    {
      category: "Language & Runtime",
      items: [
        { name: "Kotlin 2.0+", desc: "Structured concurrency & coroutines flow" },
        { name: "Android SDK 26+", desc: "Targeting Android 15 with Oreo backward compatibility" }
      ]
    },
    {
      category: "AI & Neural Compute",
      items: [
        { name: "Google Gemini 2.5 Flash", desc: "Online cloud reasoning with streaming tokens" },
        { name: "LiteRT-LM Runtime", desc: "Google AI Edge on-device engine with OpenCL GPU backend" },
        { name: "Gemma 3n (E2B & E4B)", desc: "Quantized on-device vision & language models" }
      ]
    },
    {
      category: "Persistence & Sync",
      items: [
        { name: "Room SQLite Database", desc: "Local offline-first persistence with foreign key constraints" },
        { name: "Cloud Firestore", desc: "Selective sync engine for authenticated online chats" },
        { name: "Firebase Authentication", desc: "Secure token persistence and user session state" }
      ]
    },
    {
      category: "Background & UI System",
      items: [
        { name: "Jetpack Compose (Material 3)", desc: "100% declarative UI with glassmorphism & ethereal beams" },
        { name: "Android WorkManager", desc: "Guaranteed background execution for 90-day pruning" },
        { name: "ConnectivityManager Flow", desc: "Reactive real-time network capability observation" }
      ]
    }
  ];

  return (
    <section className="py-24 relative overflow-hidden bg-[#EAE7DF] border-t border-[#DED9CE]">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        {/* Header */}
        <div className="text-center max-w-3xl mx-auto mb-16">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-[#F7F5EF] border border-[#DED9CE] text-[#424A43] text-xs font-mono mb-4 shadow-subtle">
            <Layers className="w-3.5 h-3.5 text-[#879487]" />
            <span>Modern Android Stack</span>
          </div>
          <h2 className="text-3xl sm:text-5xl font-serif font-normal text-[#171B19] tracking-tight">
            Complete Technology Stack
          </h2>
          <p className="mt-4 text-[#3A403C] text-base sm:text-lg leading-relaxed">
            Built using Google’s recommended Android modern architecture and Google AI Edge specifications.
          </p>
        </div>

        {/* Stack Grid */}
        <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-6">
          {stack.map((group, idx) => (
            <div key={idx} className="bg-[#F7F5EF] p-6 rounded-xl border border-[#DED9CE] shadow-subtle flex flex-col justify-between">
              <div>
                <h3 className="text-xs font-mono font-semibold text-[#8B704C] uppercase tracking-wider mb-4 pb-2 border-b border-[#DED9CE]">
                  {group.category}
                </h3>
                <div className="space-y-4">
                  {group.items.map((item, iIdx) => (
                    <div key={iIdx}>
                      <div className="font-bold text-[#171B19] text-sm">{item.name}</div>
                      <div className="text-xs text-[#575E59] mt-0.5 leading-relaxed">{item.desc}</div>
                    </div>
                  ))}
                </div>
              </div>

              <div className="mt-6 pt-3 border-t border-[#DED9CE] flex items-center gap-1.5 text-[11px] font-mono text-[#3C4A3C]">
                <ShieldCheck className="w-3.5 h-3.5 text-[#879487]" />
                <span className="font-medium">Production Standard</span>
              </div>
            </div>
          ))}
        </div>

      </div>
    </section>
  );
};
