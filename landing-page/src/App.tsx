import React, { useState } from 'react';
import { Navbar } from './components/Navbar';
import { Hero } from './components/Hero';
import { InteractiveSimulator } from './components/InteractiveSimulator';
import { ArchitecturePipeline } from './components/ArchitecturePipeline';
import { FeaturesGrid } from './components/FeaturesGrid';
import { BenchmarksMatrix } from './components/BenchmarksMatrix';
import { CodeDeepDive } from './components/CodeDeepDive';
import { TechStackSection } from './components/TechStackSection';
import { DownloadCTA } from './components/DownloadCTA';
import { Footer } from './components/Footer';

export default function App() {
  const [isSimulatedOnline, setIsSimulatedOnline] = useState<boolean>(true);

  return (
    <div className="min-h-screen bg-[#EAE7DF] text-[#171B19] flex flex-col font-sans">
      <Navbar 
        isSimulatedOnline={isSimulatedOnline} 
        setIsSimulatedOnline={setIsSimulatedOnline} 
      />
      <main className="flex-1">
        <Hero 
          isSimulatedOnline={isSimulatedOnline} 
          setIsSimulatedOnline={setIsSimulatedOnline} 
        />
        <InteractiveSimulator 
          isSimulatedOnline={isSimulatedOnline} 
          setIsSimulatedOnline={setIsSimulatedOnline} 
        />
        <ArchitecturePipeline />
        <FeaturesGrid />
        <BenchmarksMatrix />
        <CodeDeepDive />
        <TechStackSection />
        <DownloadCTA />
      </main>
      <Footer />
    </div>
  );
}
