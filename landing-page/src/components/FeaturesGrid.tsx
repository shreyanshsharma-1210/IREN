import React from 'react';
import { Eye, WifiOff, Shield, RefreshCw, Trash2, Smartphone, Sparkles } from 'lucide-react';

export const FeaturesGrid: React.FC = () => {
  const features = [
    {
      icon: Eye,
      title: "Multimodal Vision AI",
      tagline: "See, understand, converse",
      description: "Attach camera photos or gallery screenshots. IREN extracts structural concepts, inspects hardware, detects objects, and remembers visual context across multiple conversational turns.",
    },
    {
      icon: WifiOff,
      title: "Works 100% Offline",
      tagline: "Gemma 3n on Mobile GPU",
      description: "Download quantized Gemma 3n (E2B or E4B) directly to your device storage. Execute high-speed LLM inference in airplanes, remote research sites, or subway tunnels with zero network bars.",
    },
    {
      icon: Shield,
      title: "Zero-Knowledge Privacy Vault",
      tagline: "Air-gapped conversation safety",
      description: "Any query processed in offline mode is tagged with is_offline_only = true. The sync engine is physically hard-coded to ignore these messages, ensuring confidential thoughts never sync to Firestore.",
    },
    {
      icon: RefreshCw,
      title: "Zero-Downtime Hybrid Fallback",
      tagline: "Seamless fault recovery",
      description: "If an online Gemini request experiences API throttling, network timeout, or socket failure, ChatRepository intercepts the exception and immediately invokes LiteRT-LM without failing the user prompt.",
    },
    {
      icon: Trash2,
      title: "Automated 90-Day Auto-Prune",
      tagline: "WorkManager garbage collection",
      description: "Keeps mobile flash storage lightweight. Android WorkManager runs periodic background workers that safely clean and prune expired offline conversation tokens older than 90 days.",
    },
    {
      icon: Smartphone,
      title: "Jetpack Compose Glassmorphic UI",
      tagline: "Material 3 native craft",
      description: "Engineered from scratch using 100% Jetpack Compose with ethereal ambient glow beams, customizable light/dark/system theming, fluid spring animations, and ergonomic voice dictation.",
    }
  ];

  return (
    <section id="features" className="py-24 relative overflow-hidden bg-[#EAE7DF] border-t border-[#DED9CE]">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        {/* Header */}
        <div className="text-center max-w-3xl mx-auto mb-16">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-[#F7F5EF] border border-[#DED9CE] text-[#424A43] text-xs font-mono mb-4 shadow-subtle">
            <Sparkles className="w-3.5 h-3.5 text-[#879487]" />
            <span>Built for Android 8.0+</span>
          </div>
          <h2 className="text-3xl sm:text-5xl font-serif font-normal text-[#171B19] tracking-tight">
            Engineered for Extreme Reliability
          </h2>
          <p className="mt-4 text-[#3A403C] text-base sm:text-lg leading-relaxed">
            Every layer of IREN is crafted to bridge the divide between infinite cloud compute and zero-latency edge privacy.
          </p>
        </div>

        {/* Feature Cards Grid */}
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
          {features.map((feature, idx) => {
            const Icon = feature.icon;
            return (
              <div
                key={idx}
                className="bg-[#F7F5EF] p-7 rounded-xl border border-[#DED9CE] shadow-subtle hover:border-[#879487]/50 hover:shadow-card transition-all duration-200 flex flex-col justify-between group"
              >
                <div>
                  <div className="flex items-center justify-between mb-6">
                    <div className="w-10 h-10 rounded-lg bg-[#EAE7DF] border border-[#DED9CE] flex items-center justify-center text-[#171B19] group-hover:bg-[#171B19] group-hover:text-[#F7F5EF] transition-colors">
                      <Icon className="w-5 h-5" />
                    </div>
                    <span className="text-[10px] font-mono text-[#8B704C] uppercase tracking-wider font-medium">
                      FEATURE 0{idx + 1}
                    </span>
                  </div>

                  <h3 className="text-xl font-serif font-normal text-[#171B19] group-hover:text-[#3C4A3C] transition-colors">
                    {feature.title}
                  </h3>
                  <div className="text-xs font-mono text-[#717872] mt-1 mb-3">
                    {feature.tagline}
                  </div>
                  <p className="text-xs sm:text-[13px] text-[#575E59] leading-relaxed font-sans">
                    {feature.description}
                  </p>
                </div>

                <div className="mt-6 pt-4 border-t border-[#DED9CE] flex items-center justify-between text-xs font-mono text-[#717872]">
                  <span>Jetpack Material 3</span>
                  <span className="text-[#3C4A3C] font-semibold">Verified ✓</span>
                </div>
              </div>
            );
          })}
        </div>

      </div>
    </section>
  );
};
