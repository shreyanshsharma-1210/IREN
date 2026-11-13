import React, { useState } from 'react';
import { Wifi, WifiOff, Sparkles, Download, Menu, X } from 'lucide-react';

interface NavbarProps {
  isSimulatedOnline: boolean;
  setIsSimulatedOnline: (val: boolean) => void;
}

export const Navbar: React.FC<NavbarProps> = ({ isSimulatedOnline, setIsSimulatedOnline }) => {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  return (
    <nav className="fixed top-0 left-0 right-0 z-50 bg-[#EAE7DF]/90 backdrop-blur-md border-b border-[#DED9CE] transition-all duration-300">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-20">
          
          {/* Logo & Brand */}
          <div className="flex items-center gap-3.5">
            <div className="relative">
              <img 
                src="/iren_logo.png" 
                alt="IREN Logo" 
                className="w-10 h-10 rounded-full object-cover border border-[#B69A75]/40 p-0.5 bg-[#F7F5EF] shadow-sm"
              />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <span className="font-serif text-2xl tracking-tight text-[#171B19] font-normal">
                  IREN
                </span>
                <span className="px-2 py-0.5 text-[10px] font-mono tracking-wider uppercase rounded-sm bg-[#879487]/15 border border-[#879487]/30 text-[#4D5A4D] font-medium">
                  Edge AI
                </span>
              </div>
              <p className="text-[11px] text-[#575E59] font-sans tracking-tight hidden sm:block">
                HybridMind · On-Device &amp; Cloud Assistant
              </p>
            </div>
          </div>

          {/* Navigation Links (Desktop) */}
          <div className="hidden md:flex items-center gap-8 text-[13px] font-medium tracking-tight text-[#3A403C]">
            <a href="#overview" className="hover:text-[#171B19] transition-colors">Overview</a>
            <a href="#simulator" className="hover:text-[#171B19] transition-colors flex items-center gap-1.5">
              <span className="w-1.5 h-1.5 rounded-full bg-[#879487]"></span>
              Live Simulator
            </a>
            <a href="#architecture" className="hover:text-[#171B19] transition-colors">Architecture</a>
            <a href="#benchmarks" className="hover:text-[#171B19] transition-colors">Benchmarks</a>
            <a href="#code" className="hover:text-[#171B19] transition-colors">Source Code</a>
          </div>

          {/* Right Action Controls */}
          <div className="hidden md:flex items-center gap-3.5">
            
            {/* Live Network State Toggle Widget */}
            <button
              onClick={() => setIsSimulatedOnline(!isSimulatedOnline)}
              title="Click to toggle simulated network environment"
              className={`flex items-center gap-2 px-3 py-1.5 rounded-md text-xs font-mono transition-all duration-200 border ${
                isSimulatedOnline
                  ? 'bg-[#F7F5EF] border-[#879487]/40 text-[#3C4A3C] shadow-sm'
                  : 'bg-[#171B19] border-[#171B19] text-[#EAE7DF]'
              }`}
            >
              {isSimulatedOnline ? (
                <>
                  <span className="relative flex h-2 w-2">
                    <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-[#879487] opacity-60"></span>
                    <span className="relative inline-flex rounded-full h-2 w-2 bg-[#6E7A6E]"></span>
                  </span>
                  <Wifi className="w-3.5 h-3.5 text-[#6E7A6E]" />
                  <span className="font-medium">Gemini 2.5 Flash</span>
                </>
              ) : (
                <>
                  <span className="relative inline-flex rounded-full h-2 w-2 bg-[#B69A75]"></span>
                  <WifiOff className="w-3.5 h-3.5 text-[#B69A75]" />
                  <span className="font-medium">Offline: Gemma 3n</span>
                </>
              )}
            </button>

            {/* Download Button */}
            <a
              href="https://github.com/shreyanshsharma-1210/IREN/raw/main/release/iren-v2.5-debug.apk"
              download
              className="flex items-center gap-2 px-4 py-2 rounded-md text-xs font-medium text-[#F7F5EF] bg-[#171B19] hover:bg-[#252B28] transition-all duration-200 shadow-sm"
            >
              <Download className="w-3.5 h-3.5 text-[#B69A75]" />
              <span>Get APK</span>
            </a>
          </div>

          {/* Mobile menu trigger */}
          <div className="flex items-center gap-2 md:hidden">
            <button
              onClick={() => setIsSimulatedOnline(!isSimulatedOnline)}
              className="p-2 rounded-md bg-[#F7F5EF] border border-[#DED9CE] text-xs text-[#171B19]"
            >
              {isSimulatedOnline ? <Wifi className="w-4 h-4 text-[#6E7A6E]" /> : <WifiOff className="w-4 h-4 text-[#B69A75]" />}
            </button>
            <button
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              className="p-2 rounded-md bg-[#F7F5EF] border border-[#DED9CE] text-[#171B19]"
            >
              {mobileMenuOpen ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
            </button>
          </div>

        </div>
      </div>

      {/* Mobile Drawer */}
      {mobileMenuOpen && (
        <div className="md:hidden bg-[#F7F5EF] border-b border-[#DED9CE] px-4 pt-3 pb-6 space-y-2.5 shadow-card">
          <a 
            href="#overview" 
            onClick={() => setMobileMenuOpen(false)}
            className="block px-3 py-2 text-[#171B19] hover:bg-[#EAE7DF] rounded text-sm font-medium"
          >
            Overview
          </a>
          <a 
            href="#simulator" 
            onClick={() => setMobileMenuOpen(false)}
            className="block px-3 py-2 text-[#4D5A4D] hover:bg-[#EAE7DF] rounded text-sm font-semibold"
          >
            Live Simulator
          </a>
          <a 
            href="#architecture" 
            onClick={() => setMobileMenuOpen(false)}
            className="block px-3 py-2 text-[#171B19] hover:bg-[#EAE7DF] rounded text-sm font-medium"
          >
            Architecture
          </a>
          <a 
            href="#benchmarks" 
            onClick={() => setMobileMenuOpen(false)}
            className="block px-3 py-2 text-[#171B19] hover:bg-[#EAE7DF] rounded text-sm font-medium"
          >
            Benchmarks
          </a>
          <a 
            href="#code" 
            onClick={() => setMobileMenuOpen(false)}
            className="block px-3 py-2 text-[#171B19] hover:bg-[#EAE7DF] rounded text-sm font-medium"
          >
            Source Code
          </a>
          <div className="pt-3 border-t border-[#DED9CE] flex flex-col gap-2">
            <a
              href="https://github.com/shreyanshsharma-1210/IREN/raw/main/release/iren-v2.5-debug.apk"
              download
              onClick={() => setMobileMenuOpen(false)}
              className="flex items-center justify-center gap-2 py-2.5 rounded text-xs font-semibold bg-[#171B19] text-[#F7F5EF]"
            >
              <Download className="w-4 h-4 text-[#B69A75]" /> Download APK
            </a>
          </div>
        </div>
      )}
    </nav>
  );
};
