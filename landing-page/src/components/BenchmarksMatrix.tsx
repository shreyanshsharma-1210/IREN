import React from 'react';
import { BarChart3, Check, X, Shield, Zap } from 'lucide-react';

export const BenchmarksMatrix: React.FC = () => {
  const models = [
    {
      name: "Gemini 2.5 Flash",
      role: "Online Cloud Engine",
      badge: "Fastest Reasoning",
      badgeColor: "bg-[#879487]/15 border-[#879487]/30 text-[#3C4A3C]",
      modelSize: "Cloud (0 MB)",
      minRam: "Standard Android",
      hardwareBackend: "Google TPU v5e Cluster",
      throughput: "90 - 130 tok/s",
      multimodal: true,
      multimodalLabel: "Full Multimodal",
      offline: false,
      privacyRating: "TLS Transit (Firestore)",
      idealFor: "Deep reasoning, high-complexity questions, long web context"
    },
    {
      name: "Gemma 3n E4B",
      role: "Advanced On-Device",
      badge: "High Precision Edge",
      badgeColor: "bg-[#B69A75]/15 border-[#B69A75]/30 text-[#6B553A]",
      modelSize: "~4.1 GB",
      minRam: "6 GB Device RAM",
      hardwareBackend: "Mobile GPU (OpenCL/Vulkan)",
      throughput: "35 - 55 tok/s",
      multimodal: true,
      multimodalLabel: "LiteRT Vision (GPU)",
      offline: true,
      privacyRating: "100% Air-Gapped",
      idealFor: "Complex local reasoning, detailed image analysis, zero-cloud environments"
    },
    {
      name: "Gemma 3n E2B",
      role: "Standard On-Device",
      badge: "Efficiency Champion",
      badgeColor: "bg-[#879487]/15 border-[#879487]/30 text-[#3C4A3C]",
      modelSize: "~2.1 GB",
      minRam: "4 GB Device RAM",
      hardwareBackend: "Mobile GPU (OpenCL/Vulkan)",
      throughput: "50 - 75 tok/s",
      multimodal: true,
      multimodalLabel: "LiteRT Vision (GPU)",
      offline: true,
      privacyRating: "100% Air-Gapped",
      idealFor: "Everyday phones, fast instant answers, rapid offline transcription"
    },
    {
      name: "Gemma 2B",
      role: "Legacy On-Device",
      badge: "Ultra Lightweight",
      badgeColor: "bg-[#DED9CE]/60 border-[#DED9CE] text-[#575E59]",
      modelSize: "~1.8 GB",
      minRam: "3 GB Device RAM",
      hardwareBackend: "Mobile CPU / GPU",
      throughput: "20 - 35 tok/s",
      multimodal: false,
      multimodalLabel: "Text Only",
      offline: true,
      privacyRating: "100% Air-Gapped",
      idealFor: "Older Android hardware, low-end test devices, minimal storage devices"
    }
  ];

  return (
    <section id="benchmarks" className="py-24 relative overflow-hidden bg-[#EAE7DF] border-t border-[#DED9CE]">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        {/* Header */}
        <div className="text-center max-w-3xl mx-auto mb-16">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-[#F7F5EF] border border-[#DED9CE] text-[#424A43] text-xs font-mono mb-4 shadow-subtle">
            <BarChart3 className="w-3.5 h-3.5 text-[#879487]" />
            <span>Hardware Benchmarks</span>
          </div>
          <h2 className="text-3xl sm:text-5xl font-serif font-normal text-[#171B19] tracking-tight">
            Engine &amp; Model Comparison
          </h2>
          <p className="mt-4 text-[#3A403C] text-base sm:text-lg leading-relaxed">
            Compare resource footprints, compute delegates, and throughput metrics across IREN’s supported model weights.
          </p>
        </div>

        {/* Comparison Table */}
        <div className="bg-[#F7F5EF] rounded-xl overflow-hidden border border-[#DED9CE] shadow-subtle">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs sm:text-[13px] font-sans border-collapse">
              <thead>
                <tr className="border-b border-[#DED9CE] bg-[#EAE7DF]/70 text-[#575E59] font-mono text-[11px] uppercase tracking-wider">
                  <th className="p-4 sm:p-5 font-semibold">Model Architecture</th>
                  <th className="p-4 sm:p-5 font-semibold">Storage Size</th>
                  <th className="p-4 sm:p-5 font-semibold">RAM Requirement</th>
                  <th className="p-4 sm:p-5 font-semibold">Compute Engine</th>
                  <th className="p-4 sm:p-5 font-semibold">Token Generation</th>
                  <th className="p-4 sm:p-5 font-semibold">Vision Support</th>
                  <th className="p-4 sm:p-5 font-semibold">Offline Air-Gap</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-[#DED9CE]">
                {models.map((m, idx) => (
                  <tr key={idx} className="hover:bg-[#EAE7DF]/40 transition-colors">
                    
                    {/* Model name & role */}
                    <td className="p-4 sm:p-5">
                      <div className="font-serif font-normal text-[#171B19] text-base">{m.name}</div>
                      <div className="text-[11px] text-[#717872] mt-0.5">{m.role}</div>
                      <span className={`inline-block mt-2 px-2 py-0.5 rounded text-[10px] font-mono border ${m.badgeColor}`}>
                        {m.badge}
                      </span>
                    </td>

                    {/* Storage */}
                    <td className="p-4 sm:p-5 font-mono text-[#2C312E]">
                      {m.modelSize}
                    </td>

                    {/* RAM */}
                    <td className="p-4 sm:p-5 font-mono text-[#2C312E]">
                      {m.minRam}
                    </td>

                    {/* Backend */}
                    <td className="p-4 sm:p-5 text-[#2C312E]">
                      <span className="px-2 py-1 rounded bg-[#EAE7DF] font-mono text-[11px] border border-[#DED9CE]">
                        {m.hardwareBackend}
                      </span>
                    </td>

                    {/* Throughput */}
                    <td className="p-4 sm:p-5 font-mono font-semibold text-[#171B19]">
                      {m.throughput}
                    </td>

                    {/* Vision */}
                    <td className="p-4 sm:p-5">
                      {m.multimodal ? (
                        <div className="flex items-center gap-1.5 text-[#3C4A3C] font-medium text-xs">
                          <Check className="w-4 h-4 text-[#879487]" />
                          <span>{m.multimodalLabel}</span>
                        </div>
                      ) : (
                        <div className="flex items-center gap-1.5 text-[#8A928C] font-medium text-xs">
                          <X className="w-4 h-4" />
                          <span>Text Only</span>
                        </div>
                      )}
                    </td>

                    {/* Offline */}
                    <td className="p-4 sm:p-5">
                      {m.offline ? (
                        <div className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded text-[#3C4A3C] bg-[#879487]/15 border border-[#879487]/30 text-xs font-mono font-medium">
                          <Shield className="w-3.5 h-3.5 text-[#879487]" />
                          <span>100% Private</span>
                        </div>
                      ) : (
                        <div className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded text-[#6B553A] bg-[#B69A75]/15 border border-[#B69A75]/30 text-xs font-mono font-medium">
                          <Zap className="w-3.5 h-3.5 text-[#B69A75]" />
                          <span>Cloud Synced</span>
                        </div>
                      )}
                    </td>

                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

      </div>
    </section>
  );
};
