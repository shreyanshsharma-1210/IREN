import React from 'react';
import { Smartphone, Shield, Zap, Sparkles, Download, ArrowRight, Layers, Eye, Cpu } from 'lucide-react';

interface HeroProps {
  isSimulatedOnline: boolean;
  setIsSimulatedOnline: (val: boolean) => void;
}

export const Hero: React.FC<HeroProps> = ({ isSimulatedOnline, setIsSimulatedOnline }) => {
  return (
    <section id="overview" className="relative pt-32 pb-20 md:pt-40 md:pb-28 overflow-hidden bg-[#EAE7DF]">
      
      {/* Editorial Decorative Subtle Hairline Geometry */}
      <div className="absolute top-0 left-1/2 -translate-x-1/2 w-full max-w-7xl h-full pointer-events-none -z-10">
        <div className="absolute top-24 left-8 right-8 h-px bg-gradient-to-r from-transparent via-[#DED9CE] to-transparent" />
        <div className="absolute top-1/2 left-1/4 w-96 h-96 rounded-full bg-[#879487]/5 blur-3xl" />
        <div className="absolute top-1/3 right-1/4 w-80 h-80 rounded-full bg-[#B69A75]/5 blur-3xl" />
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid lg:grid-cols-12 gap-12 lg:gap-10 items-center">
          
          {/* Left Column: Headlines & Call to Actions */}
          <div className="lg:col-span-7 text-center lg:text-left">
            
            {/* Editorial Label Pill */}
            <div className="inline-flex items-center gap-2.5 px-3.5 py-1.5 rounded-full bg-[#F7F5EF] border border-[#DED9CE] text-[#424A43] text-xs font-mono mb-8 shadow-subtle">
              <span className="w-1.5 h-1.5 rounded-full bg-[#879487]"></span>
              <span className="font-medium tracking-tight">Next-Gen Edge AI for Android</span>
              <span className="text-[#B69A75]">/</span>
              <span className="text-[#575E59] font-sans">LiteRT-LM + Gemini 2.5</span>
            </div>

            {/* Main Headline with Editorial Display Serif */}
            <h1 className="text-4xl sm:text-6xl lg:text-[68px] font-serif tracking-tight text-[#171B19] leading-[1.08] mb-7 font-normal">
              The Hybrid AI Assistant That{' '}
              <span className="italic font-normal text-[#171B19] border-b border-[#B69A75]/50 pb-1">
                Thinks Anywhere.
              </span>
            </h1>

            {/* Subtitle */}
            <p className="text-base sm:text-lg text-[#3A403C] font-normal leading-relaxed max-w-2xl mx-auto lg:mx-0 mb-8">
              <strong className="text-[#171B19] font-semibold">IREN</strong> (Image Recognition &amp; Explanation Network) is a dual-brain Android assistant. It uses <strong className="text-[#4D5A4D] font-semibold">Google Gemini 2.5 Flash</strong> when connected and instantly drops into on-device <strong className="text-[#575E59] font-semibold">LiteRT-LM Gemma 3n</strong> when offline.
            </p>

            {/* Feature Highlights Row */}
            <div className="flex flex-wrap items-center justify-center lg:justify-start gap-2.5 mb-10 text-xs font-medium text-[#3A403C]">
              <div className="flex items-center gap-1.5 px-3 py-1.5 rounded-md bg-[#F7F5EF] border border-[#DED9CE] shadow-subtle">
                <Zap className="w-3.5 h-3.5 text-[#B69A75]" />
                <span>Zero Latency Fallback</span>
              </div>
              <div className="flex items-center gap-1.5 px-3 py-1.5 rounded-md bg-[#F7F5EF] border border-[#DED9CE] shadow-subtle">
                <Shield className="w-3.5 h-3.5 text-[#6E7A6E]" />
                <span>Private Offline Vault</span>
              </div>
              <div className="flex items-center gap-1.5 px-3 py-1.5 rounded-md bg-[#F7F5EF] border border-[#DED9CE] shadow-subtle">
                <Eye className="w-3.5 h-3.5 text-[#879487]" />
                <span>Multimodal Vision</span>
              </div>
              <div className="flex items-center gap-1.5 px-3 py-1.5 rounded-md bg-[#F7F5EF] border border-[#DED9CE] shadow-subtle">
                <Cpu className="w-3.5 h-3.5 text-[#575E59]" />
                <span>On-Device GPU Inference</span>
              </div>
            </div>

            {/* CTA Buttons */}
            <div className="flex flex-col sm:flex-row items-center justify-center lg:justify-start gap-3.5">
              <a
                href="#simulator"
                className="w-full sm:w-auto px-7 py-3.5 rounded-md font-medium text-sm text-[#F7F5EF] bg-[#879487] hover:bg-[#6E7A6E] transition-all duration-200 shadow-card flex items-center justify-center gap-2 group"
              >
                <span>Launch Live Simulator</span>
                <ArrowRight className="w-4 h-4 text-[#F7F5EF] group-hover:translate-x-1 transition-transform" />
              </a>

              <a
                href="#architecture"
                className="w-full sm:w-auto px-6 py-3.5 rounded-md font-medium text-sm text-[#171B19] bg-[#F7F5EF] border border-[#DED9CE] hover:border-[#171B19]/30 hover:bg-[#F1EFE8] transition-all flex items-center justify-center gap-2 shadow-subtle"
              >
                <Layers className="w-4 h-4 text-[#879487]" />
                <span>Explore Architecture</span>
              </a>

              <a
                href="https://github.com/shreyanshsharma-1210/IREN/raw/main/release/iren-v2.5-debug.apk"
                download
                className="w-full sm:w-auto px-5 py-3.5 rounded-md font-medium text-sm text-[#424A43] hover:text-[#171B19] hover:bg-[#E1DDD3]/60 transition-colors flex items-center justify-center gap-2"
              >
                <Download className="w-4 h-4 text-[#B69A75]" />
                <span>Download APK</span>
              </a>
            </div>

            {/* Under CTA Note */}
            <p className="text-[11px] text-[#717872] font-mono mt-4 text-center lg:text-left">
              Compatible with Android 8.0+ (Oreo to Android 15) · Open Source MIT
            </p>

          </div>

          {/* Right Column: Mobile Phone Mockup */}
          <div className="lg:col-span-5 flex justify-center">
            <div className="relative w-full max-w-[340px]">
              
              {/* Subtle Depth Shadow */}
              <div className="absolute inset-4 rounded-[40px] bg-[#171B19]/10 blur-2xl -z-10" />
              
              {/* Device Frame */}
              <div className="relative rounded-[38px] border border-[#B69A75]/35 bg-[#171B19] shadow-darkCard p-2.5">
                
                {/* Notch & Status Bar */}
                <div className="h-5 w-full flex items-center justify-between px-5 pt-1 text-[10px] font-mono text-[#DCD7CD]">
                  <span>9:41</span>
                  <div className="w-16 h-2.5 bg-[#252B28] rounded-full"></div>
                  <div className="flex items-center gap-1">
                    {isSimulatedOnline ? <Zap className="w-3 h-3 text-[#879487]" /> : <Shield className="w-3 h-3 text-[#B69A75]" />}
                    <span>100%</span>
                  </div>
                </div>

                {/* Android App Screen Container */}
                <div className="rounded-[26px] bg-[#F7F5EF] border border-[#DED9CE] p-4 min-h-[500px] flex flex-col justify-between text-left">
                  
                  {/* App Header */}
                  <div>
                    <div className="flex items-center justify-between pb-3 border-b border-[#DED9CE]">
                      <div className="flex items-center gap-2">
                        <img src="/iren_logo.png" alt="IREN" className="w-7 h-7 rounded-full border border-[#B69A75]/40" />
                        <div>
                          <div className="text-xs font-bold text-[#171B19] tracking-tight">IREN Assistant</div>
                          <div className="text-[10px] text-[#717872] font-mono">v2.5 Hybrid Release</div>
                        </div>
                      </div>

                      {/* Phone Interactive Toggle Button */}
                      <button
                        onClick={() => setIsSimulatedOnline(!isSimulatedOnline)}
                        className={`px-2.5 py-1 rounded text-[10px] font-mono font-medium flex items-center gap-1.5 transition-all ${
                          isSimulatedOnline 
                            ? 'bg-[#879487]/15 border border-[#879487]/40 text-[#3C4A3C]' 
                            : 'bg-[#B69A75]/15 border border-[#B69A75]/40 text-[#6B553A]'
                        }`}
                      >
                        <span className={`w-1.5 h-1.5 rounded-full ${isSimulatedOnline ? 'bg-[#6E7A6E]' : 'bg-[#B69A75]'}`}></span>
                        {isSimulatedOnline ? 'Online (Gemini)' : 'Offline (Gemma)'}
                      </button>
                    </div>

                    {/* Routing State Alert Banner */}
                    <div className={`mt-3 p-2.5 rounded-md border text-[11px] leading-relaxed transition-all ${
                      isSimulatedOnline
                        ? 'bg-[#EAE7DF] border-[#DED9CE] text-[#2C312E]'
                        : 'bg-[#F1EFE8] border-[#B69A75]/30 text-[#4A3B2A]'
                    }`}>
                      <div className="font-semibold flex items-center gap-1.5 mb-0.5 text-xs">
                        <span className="w-1.5 h-1.5 rounded-full bg-[#879487]"></span>
                        {isSimulatedOnline ? 'Cloud Intelligence Active' : 'Edge Mode: 100% On-Device'}
                      </div>
                      <p className="text-[10px] text-[#575E59]">
                        {isSimulatedOnline 
                          ? 'Routed to Google Gemini 2.5 Flash via Generative AI SDK with Firestore sync.' 
                          : 'Zero cloud transit. In-memory GPU inference via LiteRT-LM & Room DB.'}
                      </p>
                    </div>

                    {/* Chat Message History */}
                    <div className="mt-4 space-y-3">
                      
                      {/* User message */}
                      <div className="flex justify-end">
                        <div className="max-w-[85%] p-3 rounded-xl rounded-tr-none bg-[#171B19] text-[#F7F5EF] text-xs leading-relaxed shadow-sm">
                          <p className="font-normal">Explain quantum superposition and verify this offline privacy guarantee.</p>
                        </div>
                      </div>

                      {/* AI Response Bubble */}
                      <div className="flex gap-2 items-start">
                        <div className="w-6 h-6 rounded-full bg-[#879487]/20 border border-[#879487]/40 flex items-center justify-center shrink-0 mt-0.5">
                          <Sparkles className="w-3.5 h-3.5 text-[#576357]" />
                        </div>
                        <div className="max-w-[88%] p-3 rounded-xl rounded-tl-none bg-[#EAE7DF] border border-[#DED9CE] text-[#171B19] text-xs leading-relaxed shadow-subtle">
                          {isSimulatedOnline ? (
                            <p>
                              <span className="text-[#576357] font-mono text-[10px] font-semibold block mb-1">⚡ Gemini 2.5 Flash (182ms)</span>
                              Quantum superposition allows particles to exist in multiple linear state combinations simultaneously until measurement collapses the wave function.
                            </p>
                          ) : (
                            <p>
                              <span className="text-[#8B704C] font-mono text-[10px] font-semibold block mb-1">🔒 LiteRT-LM Gemma 3n (Local GPU • Private)</span>
                              Superposition means a quantum state exists in all possible configurations simultaneously. <span className="text-[#3C4A3C] font-semibold">This message is stored strictly in local Room DB and will never sync to cloud.</span>
                            </p>
                          )}
                        </div>
                      </div>

                    </div>
                  </div>

                  {/* Mock Input Bar */}
                  <div className="mt-4 pt-3 border-t border-[#DED9CE]">
                    <div className="flex items-center gap-2 p-2 rounded-md bg-[#EAE7DF] border border-[#DED9CE] text-xs text-[#575E59]">
                      <span className="p-1 rounded bg-[#F7F5EF] text-[#879487]">
                        <Eye className="w-3.5 h-3.5" />
                      </span>
                      <span className="flex-1 truncate text-[11px]">Ask anything or capture photo...</span>
                      <span className="w-5 h-5 rounded bg-[#171B19] flex items-center justify-center text-[#F7F5EF] font-bold text-[10px]">
                        ↑
                      </span>
                    </div>
                  </div>

                </div>

              </div>

            </div>
          </div>

        </div>

        {/* Bottom Metrics Bar */}
        <div className="mt-16 pt-10 border-t border-[#DED9CE] grid grid-cols-2 md:grid-cols-4 gap-4 sm:gap-6 text-center">
          <div className="bg-[#F7F5EF] p-5 rounded-lg border border-[#DED9CE] shadow-subtle">
            <div className="text-2xl md:text-3xl font-serif text-[#171B19]">0 ms</div>
            <div className="text-[10px] sm:text-xs text-[#575E59] mt-1 uppercase tracking-wider font-mono font-medium">Offline Latency Overhead</div>
          </div>
          <div className="bg-[#F7F5EF] p-5 rounded-lg border border-[#DED9CE] shadow-subtle">
            <div className="text-2xl md:text-3xl font-serif text-[#4D5A4D]">100%</div>
            <div className="text-[10px] sm:text-xs text-[#575E59] mt-1 uppercase tracking-wider font-mono font-medium">Zero-Cloud Privacy Guarantee</div>
          </div>
          <div className="bg-[#F7F5EF] p-5 rounded-lg border border-[#DED9CE] shadow-subtle">
            <div className="text-2xl md:text-3xl font-serif text-[#171B19]">2.1 - 4.1 GB</div>
            <div className="text-[10px] sm:text-xs text-[#575E59] mt-1 uppercase tracking-wider font-mono font-medium">Gemma 3n Quantized Weights</div>
          </div>
          <div className="bg-[#F7F5EF] p-5 rounded-lg border border-[#DED9CE] shadow-subtle">
            <div className="text-2xl md:text-3xl font-serif text-[#8B704C]">90 Days</div>
            <div className="text-[10px] sm:text-xs text-[#575E59] mt-1 uppercase tracking-wider font-mono font-medium">Automated WorkManager GC</div>
          </div>
        </div>

      </div>
    </section>
  );
};
