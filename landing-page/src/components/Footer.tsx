import React from 'react';
import { ArrowUp } from 'lucide-react';

export const Footer: React.FC = () => {
  const scrollToTop = () => {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  return (
    <footer className="border-t border-[#2C312E] bg-[#171B19] py-14 text-xs font-mono text-[#A3B0A3]">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        <div className="flex flex-col md:flex-row items-center justify-between gap-6 pb-8 border-b border-[#252B28]">
          
          {/* Logo & Tagline */}
          <div className="flex items-center gap-3.5">
            <img src="/iren_logo.png" alt="IREN Logo" className="w-8 h-8 rounded-full border border-[#B69A75]/40 p-0.5 bg-[#252B28]" />
            <div>
              <div className="font-serif text-lg text-[#F7F5EF] tracking-tight">IREN · HYBRIDMIND</div>
              <div className="text-[11px] text-[#8A928C] font-sans">Image Recognition &amp; Explanation Network</div>
            </div>
          </div>

          {/* Quick Links */}
          <div className="flex flex-wrap items-center justify-center gap-6 text-[#DCD7CD] font-sans text-[13px]">
            <a href="#overview" className="hover:text-[#F7F5EF] transition-colors">Overview</a>
            <a href="#simulator" className="hover:text-[#F7F5EF] transition-colors">Live Simulator</a>
            <a href="#architecture" className="hover:text-[#F7F5EF] transition-colors">Architecture</a>
            <a href="#benchmarks" className="hover:text-[#F7F5EF] transition-colors">Benchmarks</a>
            <a href="#code" className="hover:text-[#F7F5EF] transition-colors">Code</a>
          </div>

          {/* Scroll to Top */}
          <button
            onClick={scrollToTop}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded bg-[#242926] hover:bg-[#2E3531] border border-[#3A423D] text-[#DCD7CD] hover:text-[#F7F5EF] transition-colors"
          >
            <span>Back to top</span>
            <ArrowUp className="w-3.5 h-3.5 text-[#B69A75]" />
          </button>

        </div>

        <div className="pt-8 flex flex-col sm:flex-row items-center justify-between gap-4 text-center sm:text-left">
          <p className="text-[#8A928C] text-[11px] font-sans">
            Designed &amp; Engineered by <strong className="text-[#F7F5EF] font-medium">Shreyansh</strong> · Part of the <strong className="text-[#B69A75] font-medium">ORB. Studio</strong> portfolio.
          </p>
          <div className="flex items-center gap-4 text-[11px]">
            <span>License: <strong className="text-[#F7F5EF] font-medium">MIT</strong></span>
            <span>·</span>
            <span>Target: <strong className="text-[#F7F5EF] font-medium">Android 8.0 - 15</strong></span>
          </div>
        </div>

      </div>
    </footer>
  );
};
