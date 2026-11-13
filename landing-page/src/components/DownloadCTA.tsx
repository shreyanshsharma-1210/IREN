import React from 'react';
import { Download, Github, Smartphone, ArrowUpRight } from 'lucide-react';

export const DownloadCTA: React.FC = () => {
  return (
    <section id="download" className="py-24 relative overflow-hidden bg-[#171B19] text-[#F7F5EF]">
      
      {/* Editorial Decorative Geometry */}
      <div className="absolute inset-0 pointer-events-none -z-10">
        <div className="absolute top-0 left-0 right-0 h-px bg-gradient-to-r from-transparent via-[#B69A75]/30 to-transparent" />
        <div className="absolute bottom-0 left-0 right-0 h-px bg-gradient-to-r from-transparent via-[#879487]/20 to-transparent" />
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        <div className="bg-[#1E2320] p-8 sm:p-12 lg:p-16 rounded-2xl border border-[#B69A75]/25 shadow-darkCard">
          <div className="grid lg:grid-cols-12 gap-12 items-center">
            
            {/* Left Column: CTA Pitch */}
            <div className="lg:col-span-7 text-center lg:text-left">
              <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-[#252B28] border border-[#B69A75]/30 text-[#DCD7CD] text-xs font-mono mb-6">
                <Smartphone className="w-3.5 h-3.5 text-[#B69A75]" />
                <span>Ready for Android</span>
              </div>

              <h2 className="text-3xl sm:text-5xl font-serif font-normal text-[#F7F5EF] tracking-tight leading-tight">
                Get Started with IREN Today.
              </h2>

              <p className="mt-4 text-base sm:text-lg text-[#DCD7CD] max-w-xl mx-auto lg:mx-0 leading-relaxed font-sans">
                Download the Android release APK or clone the Kotlin repository to run on your device or in Android Studio Ladybug+.
              </p>

              {/* Steps list */}
              <div className="mt-8 space-y-3.5 max-w-md mx-auto lg:mx-0 text-left">
                <div className="flex items-start gap-3 text-sm text-[#EAE7DF]">
                  <div className="w-5 h-5 rounded-full bg-[#879487]/25 text-[#A3B0A3] flex items-center justify-center text-xs font-mono font-bold shrink-0 mt-0.5 border border-[#879487]/40">
                    1
                  </div>
                  <span>Install APK or run <code className="text-[#F7F5EF] font-mono text-xs bg-[#252B28] px-2 py-0.5 rounded border border-[#3A423D]">./gradlew assembleDebug</code></span>
                </div>
                <div className="flex items-start gap-3 text-sm text-[#EAE7DF]">
                  <div className="w-5 h-5 rounded-full bg-[#879487]/25 text-[#A3B0A3] flex items-center justify-center text-xs font-mono font-bold shrink-0 mt-0.5 border border-[#879487]/40">
                    2
                  </div>
                  <span>Add your Gemini API key &amp; connect Firebase Auth</span>
                </div>
                <div className="flex items-start gap-3 text-sm text-[#EAE7DF]">
                  <div className="w-5 h-5 rounded-full bg-[#879487]/25 text-[#A3B0A3] flex items-center justify-center text-xs font-mono font-bold shrink-0 mt-0.5 border border-[#879487]/40">
                    3
                  </div>
                  <span>Download Gemma 3n offline weights directly inside the app</span>
                </div>
              </div>

              {/* Action Buttons */}
              <div className="mt-10 flex flex-col sm:flex-row items-center justify-center lg:justify-start gap-3.5">
                <a
                  href="https://github.com/shreyanshsharma-1210/IREN/raw/main/release/iren-v2.5-debug.apk"
                  download
                  className="w-full sm:w-auto px-7 py-3.5 rounded-md font-medium text-sm text-[#F7F5EF] bg-[#879487] hover:bg-[#6E7A6E] transition-all duration-200 shadow-sm flex items-center justify-center gap-2"
                >
                  <Download className="w-4 h-4 text-[#F7F5EF]" />
                  <span>Download Release APK</span>
                </a>

                <a
                  href="https://github.com/shreyanshsharma-1210/IREN"
                  target="_blank"
                  rel="noreferrer"
                  className="w-full sm:w-auto px-6 py-3.5 rounded-md font-medium text-sm text-[#EAE7DF] bg-[#252B28] hover:bg-[#2E3531] border border-[#B69A75]/30 transition-all flex items-center justify-center gap-2"
                >
                  <Github className="w-4 h-4 text-[#B69A75]" />
                  <span>View on GitHub</span>
                  <ArrowUpRight className="w-4 h-4 text-[#8A928C]" />
                </a>
              </div>

            </div>

            {/* Right Column: Logo showcase */}
            <div className="lg:col-span-5 flex justify-center">
              <div className="relative p-7 rounded-xl bg-[#242926] border border-[#B69A75]/30 max-w-sm text-center shadow-subtle">
                <div className="relative mx-auto w-32 h-32 rounded-full p-1 border border-[#B69A75]/40 mb-6 bg-[#171B19]">
                  <img 
                    src="/iren_logo.png" 
                    alt="IREN Project Logo" 
                    className="w-full h-full rounded-full object-cover p-1 bg-[#171B19]"
                  />
                </div>
                <h3 className="text-xl font-serif font-normal text-[#F7F5EF]">IREN / HybridMind</h3>
                <p className="text-xs text-[#B69A75] font-mono mt-1">Image Recognition &amp; Explanation Network</p>
                <div className="mt-4 pt-4 border-t border-[#3A423D] text-xs text-[#A3B0A3] leading-relaxed font-serif italic text-base">
                  &ldquo;Turning edge devices into air-gapped sovereign AI brains.&rdquo;
                </div>
              </div>
            </div>

          </div>
        </div>

      </div>
    </section>
  );
};
