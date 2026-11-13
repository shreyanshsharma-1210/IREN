import React, { useState, useEffect } from 'react';
import { Wifi, WifiOff, Send, Cpu, RefreshCw, Eye } from 'lucide-react';

interface InteractiveSimulatorProps {
  isSimulatedOnline: boolean;
  setIsSimulatedOnline: (val: boolean) => void;
}

interface PresetQuery {
  id: string;
  category: string;
  label: string;
  prompt: string;
  hasImage?: boolean;
  imageLabel?: string;
  onlineResponse: string;
  offlineResponse: string;
}

const PRESET_QUERIES: PresetQuery[] = [
  {
    id: 'vision-analysis',
    category: 'Multimodal Vision',
    label: 'Identify PCB Components',
    hasImage: true,
    imageLabel: 'circuit_board_macro.jpg',
    prompt: 'Identify the microcontrollers and passives on this development board and check for soldering bridge hazards.',
    onlineResponse: `[Gemini 2.5 Flash Multimodal Vision Output]
1. Primary IC: ESP32-WROOM-32D Dual-Core MCU (240MHz, 2.4GHz Wi-Fi & BLE).
2. Voltage Regulator: AMS1117-3.3V Low-Dropout linear regulator in SOT-223 package.
3. Passives: Array of 0805 SMD decoupling capacitors (10µF & 100nF) adjacent to VDD rails.
4. Soldering Inspection: No solder bridges detected around QFN pins. Clean fillet transitions across all solder joints.
[Sync: Firestore document 'sessions/sess_91a/msg_04' committed in 142ms]`,
    offlineResponse: `[LiteRT-LM Gemma 3n On-Device Vision Engine]
✓ Model: Gemma 3n E4B (Vision Backend: GPU)
1. Detected: Microcontroller unit (ESP32 series) with integrated RF shield.
2. Detected: 3.3V power regulator module and standard USB-to-UART bridge.
3. Passives: Surface-mount capacitor and resistor bank in standard layout.
4. Inspection: Pin leads appear isolated with no obvious short-circuits.
[🔒 Privacy Notice: Stored exclusively in local Room SQLite. Flag: is_offline_only = true. Zero bytes transmitted to cloud.]`,
  },
  {
    id: 'hybrid-routing',
    category: 'Architecture Test',
    label: 'How Hybrid Fallback Works',
    prompt: 'Explain how IREN maintains continuous conversation if the network drops mid-stream.',
    onlineResponse: `[Gemini 2.5 Flash Response]
IREN leverages Android Coroutines Flow with NetworkMonitor.kt wrapping ConnectivityManager. The ChatRepository initiates generation using Gemini streaming (gemini-2.5-flash). If the network drops or the Gemini client throws an IOException, the repository catches the failure and immediately dispatches the identical prompt + conversational context into LiteRT-LM running locally. The user experiences zero interruption or crash.`,
    offlineResponse: `[LiteRT-LM Gemma 3n On-Device Response]
IREN detects zero network availability prior to dispatch. Rather than attempting a network socket, ChatRepository directly invokes Engine.createConversation() using the local model file in /data/user/0/.../app_models/gemma-3n.bin. The generation runs entirely across your device's GPU shaders without network overhead. The session is tagged 'is_offline_only = true'.`,
  },
  {
    id: 'privacy-test',
    category: 'Privacy & Security',
    label: 'Confidential Medical Query',
    prompt: 'Summarize blood panel: HbA1c 5.8%, fasting glucose 102 mg/dL, Total Cholesterol 185 mg/dL.',
    onlineResponse: `[Gemini 2.5 Flash Response]
Summary:
- HbA1c 5.8%: Pre-diabetes range (5.7% - 6.4%).
- Fasting Glucose 102 mg/dL: Slightly elevated impaired fasting glucose (100 - 125 mg/dL).
- Total Cholesterol 185 mg/dL: Desirable (<200 mg/dL).
Note: Consult your physician for clinical interpretation.
[Cloud Record: Synced to encrypted Firestore user profile]`,
    offlineResponse: `[LiteRT-LM Gemma 3n Edge Response]
Summary (Processed 100% on device):
- HbA1c 5.8% sits in the early pre-diabetic threshold.
- Fasting glucose 102 mg/dL indicates mild impaired fasting glycemia.
- Lipid panel at 185 mg/dL is within typical healthy parameters.
[🔒 ZERO-KNOWLEDGE EDGE AUDIT: This sensitive medical query was processed in device volatile RAM and written only to encrypted Room SQLite. No third-party API or analytics server received this text.]`,
  },
];

export const InteractiveSimulator: React.FC<InteractiveSimulatorProps> = ({
  isSimulatedOnline,
  setIsSimulatedOnline,
}) => {
  const [selectedPreset, setSelectedPreset] = useState<PresetQuery>(PRESET_QUERIES[0]);
  const [userQuery, setUserQuery] = useState(PRESET_QUERIES[0].prompt);
  const [isGenerating, setIsGenerating] = useState(false);
  const [displayedResponse, setDisplayedResponse] = useState('');
  const [tokenCounter, setTokenCounter] = useState(0);

  const handleSelectPreset = (preset: PresetQuery) => {
    setSelectedPreset(preset);
    setUserQuery(preset.prompt);
    triggerGeneration(preset, isSimulatedOnline);
  };

  const triggerGeneration = (preset: PresetQuery, onlineState: boolean) => {
    setIsGenerating(true);
    setDisplayedResponse('');
    setTokenCounter(0);

    const fullText = onlineState ? preset.onlineResponse : preset.offlineResponse;
    const speedMs = onlineState ? 14 : 22;
    let currentIdx = 0;

    const interval = setInterval(() => {
      if (currentIdx < fullText.length) {
        currentIdx += 4;
        setDisplayedResponse(fullText.slice(0, currentIdx));
        setTokenCounter(Math.floor(currentIdx / 4));
      } else {
        setDisplayedResponse(fullText);
        setIsGenerating(false);
        clearInterval(interval);
      }
    }, speedMs);
  };

  useEffect(() => {
    triggerGeneration(selectedPreset, isSimulatedOnline);
  }, [isSimulatedOnline]);

  return (
    <section id="simulator" className="py-24 relative overflow-hidden bg-[#EAE7DF] border-t border-[#DED9CE]">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        {/* Section Header */}
        <div className="text-center max-w-3xl mx-auto mb-14">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-[#F7F5EF] border border-[#DED9CE] text-[#424A43] text-xs font-mono mb-4 shadow-subtle">
            <Cpu className="w-3.5 h-3.5 text-[#879487]" />
            <span>Interactive Simulator</span>
          </div>
          <h2 className="text-3xl sm:text-5xl font-serif font-normal text-[#171B19] tracking-tight">
            Test the Dual Brain in Real-Time
          </h2>
          <p className="mt-4 text-[#3A403C] text-base sm:text-lg leading-relaxed">
            Toggle network connectivity below to see how IREN alters routing, latency benchmarks, hardware backends, and privacy boundaries.
          </p>
        </div>

        {/* Master Control Bar */}
        <div className="bg-[#F7F5EF] p-5 sm:p-6 rounded-xl border border-[#DED9CE] shadow-subtle mb-8">
          <div className="flex flex-col md:flex-row items-center justify-between gap-6">
            
            {/* Network Environment Toggle Switch */}
            <div className="flex items-center gap-4 w-full md:w-auto">
              <span className="text-xs font-mono uppercase tracking-wider text-[#575E59]">Simulated Network:</span>
              <div className="flex items-center bg-[#EAE7DF] p-1 rounded-lg border border-[#DED9CE] w-full sm:w-auto">
                
                <button
                  onClick={() => setIsSimulatedOnline(true)}
                  className={`flex-1 sm:flex-initial flex items-center justify-center gap-2 px-4 py-2 rounded-md text-xs font-medium transition-all ${
                    isSimulatedOnline
                      ? 'bg-[#879487] text-[#F7F5EF] shadow-sm'
                      : 'text-[#575E59] hover:text-[#171B19]'
                  }`}
                >
                  <Wifi className="w-3.5 h-3.5" />
                  <span>Online (Cloud 5G)</span>
                </button>

                <button
                  onClick={() => setIsSimulatedOnline(false)}
                  className={`flex-1 sm:flex-initial flex items-center justify-center gap-2 px-4 py-2 rounded-md text-xs font-medium transition-all ${
                    !isSimulatedOnline
                      ? 'bg-[#171B19] text-[#F7F5EF] shadow-sm'
                      : 'text-[#575E59] hover:text-[#171B19]'
                  }`}
                >
                  <WifiOff className="w-3.5 h-3.5 text-[#B69A75]" />
                  <span>Offline (Airplane Mode)</span>
                </button>

              </div>
            </div>

            {/* Active Mode Telemetry Badges */}
            <div className="flex flex-wrap items-center gap-2.5 w-full md:w-auto justify-end">
              <div className="flex items-center gap-2 px-3 py-1.5 rounded bg-[#EAE7DF] border border-[#DED9CE] text-xs font-mono">
                <span className="text-[#717872]">Model:</span>
                <span className={isSimulatedOnline ? 'text-[#4D5A4D] font-semibold' : 'text-[#8B704C] font-semibold'}>
                  {isSimulatedOnline ? 'Gemini 2.5 Flash' : 'LiteRT-LM Gemma 3n'}
                </span>
              </div>

              <div className="flex items-center gap-2 px-3 py-1.5 rounded bg-[#EAE7DF] border border-[#DED9CE] text-xs font-mono">
                <span className="text-[#717872]">Compute Backend:</span>
                <span className="text-[#171B19] font-medium">
                  {isSimulatedOnline ? 'Google TPU Cluster' : 'Mobile GPU (OpenCL)'}
                </span>
              </div>

              <div className="flex items-center gap-2 px-3 py-1.5 rounded bg-[#EAE7DF] border border-[#DED9CE] text-xs font-mono">
                <span className="text-[#717872]">Privacy Status:</span>
                <span className={isSimulatedOnline ? 'text-[#8B704C] font-medium' : 'text-[#3C4A3C] font-semibold'}>
                  {isSimulatedOnline ? 'Cloud Synced' : '100% Device-Isolated'}
                </span>
              </div>
            </div>

          </div>
        </div>

        {/* Preset Queries Selectors */}
        <div className="flex flex-wrap items-center gap-2.5 mb-6">
          <span className="text-xs font-mono text-[#575E59] mr-1">Try Presets:</span>
          {PRESET_QUERIES.map((preset) => (
            <button
              key={preset.id}
              onClick={() => handleSelectPreset(preset)}
              className={`px-3.5 py-2 rounded-md text-xs font-medium transition-all flex items-center gap-2 border ${
                selectedPreset.id === preset.id
                  ? 'bg-[#F7F5EF] border-[#879487] text-[#171B19] shadow-subtle'
                  : 'bg-[#EAE7DF] border-[#DED9CE] text-[#575E59] hover:text-[#171B19] hover:bg-[#F1EFE8]'
              }`}
            >
              {preset.hasImage && <Eye className="w-3.5 h-3.5 text-[#879487]" />}
              <span>{preset.label}</span>
              <span className="text-[10px] px-1.5 py-0.5 rounded bg-[#DED9CE]/60 text-[#424A43] font-mono">
                {preset.category}
              </span>
            </button>
          ))}
        </div>

        {/* Interactive Query & Response Terminal */}
        <div className="grid lg:grid-cols-12 gap-6">
          
          {/* Left Column: Input & Context */}
          <div className="lg:col-span-5 flex flex-col gap-4">
            
            <div className="bg-[#F7F5EF] p-5 rounded-xl border border-[#DED9CE] shadow-subtle flex-1 flex flex-col justify-between">
              <div>
                <div className="flex items-center justify-between mb-3 text-xs font-mono text-[#575E59]">
                  <span className="uppercase tracking-wider">DISPATCH CONSOLE</span>
                  <span className="text-[#4D5A4D] font-semibold">{isSimulatedOnline ? 'ONLINE ROUTE' : 'LOCAL FALLBACK'}</span>
                </div>

                {/* Attached Image Card */}
                {selectedPreset.hasImage && (
                  <div className="mb-4 p-3 rounded-md bg-[#EAE7DF] border border-[#DED9CE] flex items-center gap-3">
                    <div className="w-10 h-10 rounded bg-[#F7F5EF] border border-[#B69A75]/40 flex items-center justify-center shrink-0">
                      <Eye className="w-5 h-5 text-[#879487]" />
                    </div>
                    <div className="text-xs">
                      <div className="font-semibold text-[#171B19]">{selectedPreset.imageLabel}</div>
                      <div className="text-[10px] text-[#575E59] font-mono">Multimodal Vision Tensor (224x224 RGB)</div>
                    </div>
                  </div>
                )}

                <label className="text-xs font-semibold text-[#171B19] block mb-2">Prompt Payload:</label>
                <textarea
                  value={userQuery}
                  onChange={(e) => setUserQuery(e.target.value)}
                  rows={4}
                  className="w-full p-3 rounded-md bg-[#EAE7DF] border border-[#DED9CE] text-xs text-[#171B19] font-mono focus:outline-none focus:border-[#879487] resize-none"
                  placeholder="Enter custom prompt..."
                />
              </div>

              <div className="mt-4 pt-4 border-t border-[#DED9CE] flex items-center justify-between">
                <button
                  onClick={() => triggerGeneration(selectedPreset, isSimulatedOnline)}
                  disabled={isGenerating}
                  className="px-5 py-2 rounded-md font-medium text-xs text-[#F7F5EF] bg-[#879487] hover:bg-[#6E7A6E] disabled:opacity-50 transition-all flex items-center gap-2"
                >
                  {isGenerating ? <RefreshCw className="w-3.5 h-3.5 animate-spin" /> : <Send className="w-3.5 h-3.5" />}
                  <span>{isGenerating ? 'Inferring...' : 'Send to Router'}</span>
                </button>

                <div className="text-[11px] font-mono text-[#717872]">
                  Target: {isSimulatedOnline ? 'Gemini 2.5 API' : 'LiteRT-LM Engine'}
                </div>
              </div>
            </div>

            {/* Architecture Decision Audit Card */}
            <div className="bg-[#F7F5EF] p-4 rounded-xl border border-[#DED9CE] text-xs space-y-2 shadow-subtle">
              <div className="font-mono text-[#575E59] font-semibold flex items-center justify-between text-[11px]">
                <span className="uppercase tracking-wider">ROUTER DECISION TREE</span>
                <span className="text-[#879487]">ChatRepository.kt:178</span>
              </div>
              <div className="p-3 rounded bg-[#EAE7DF] font-mono text-[11px] text-[#171B19] border border-[#DED9CE] space-y-1">
                <div>1. NetworkMonitor.isOnline.first() == <strong className={isSimulatedOnline ? 'text-[#3C4A3C]' : 'text-[#8B704C]'}>{isSimulatedOnline ? 'true' : 'false'}</strong></div>
                <div>2. Session flag is_offline_only == <strong>{isSimulatedOnline ? 'false' : 'true'}</strong></div>
                <div>3. Selected Dispatch: <strong className={isSimulatedOnline ? 'text-[#4D5A4D]' : 'text-[#8B704C]'}>{isSimulatedOnline ? 'generateWithGeminiStream()' : 'generateWithMediaPipe()'}</strong></div>
              </div>
            </div>

          </div>

          {/* Right Column: Output Stream Terminal (Graphite Dark Surface) */}
          <div className="lg:col-span-7">
            <div className="bg-[#171B19] p-5 sm:p-6 rounded-xl h-full flex flex-col justify-between border border-[#B69A75]/25 shadow-darkCard">
              
              <div>
                {/* Terminal Header */}
                <div className="flex items-center justify-between pb-3 border-b border-[#2C312E] text-xs font-mono">
                  <div className="flex items-center gap-2">
                    <span className={`w-2 h-2 rounded-full ${isGenerating ? 'bg-[#B69A75] animate-ping' : isSimulatedOnline ? 'bg-[#879487]' : 'bg-[#B69A75]'}`}></span>
                    <span className="text-[#F7F5EF] font-medium">
                      {isSimulatedOnline ? 'Gemini 2.5 Flash Stream' : 'LiteRT-LM Gemma 3n Output'}
                    </span>
                  </div>

                  <div className="flex items-center gap-3 text-[#A3B0A3] text-[11px]">
                    <span>Tokens: <strong className="text-[#F7F5EF] font-mono">{tokenCounter}</strong></span>
                    <span>Speed: <strong className="text-[#B69A75] font-mono">{isSimulatedOnline ? '~95 tok/s' : '~42 tok/s'}</strong></span>
                  </div>
                </div>

                {/* Stream Content Area */}
                <div className="mt-4 min-h-[250px] p-4 rounded-lg bg-[#1E2320] border border-[#2C312E] font-mono text-xs sm:text-[13px] text-[#EAE7DF] leading-relaxed whitespace-pre-wrap">
                  {displayedResponse}
                  {isGenerating && <span className="inline-block w-1.5 h-3.5 ml-1 bg-[#879487] animate-pulse"></span>}
                </div>
              </div>

              {/* Terminal Bottom Audit Info */}
              <div className="mt-4 pt-4 border-t border-[#2C312E] grid grid-cols-2 sm:grid-cols-3 gap-2.5 text-[11px] font-mono">
                <div className="p-2.5 rounded bg-[#242926] border border-[#2C312E]">
                  <span className="text-[#8A928C] block text-[10px]">Roundtrip Latency</span>
                  <span className="text-[#F7F5EF] font-semibold">{isSimulatedOnline ? '142 ms (Cloud)' : '38 ms (RAM Direct)'}</span>
                </div>
                <div className="p-2.5 rounded bg-[#242926] border border-[#2C312E]">
                  <span className="text-[#8A928C] block text-[10px]">Persistence Target</span>
                  <span className="text-[#F7F5EF] font-semibold">{isSimulatedOnline ? 'Firestore + Room' : 'Room DB (Local Only)'}</span>
                </div>
                <div className="p-2.5 rounded bg-[#242926] border border-[#2C312E] col-span-2 sm:col-span-1">
                  <span className="text-[#8A928C] block text-[10px]">Cloud Zero-Knowledge</span>
                  <span className={isSimulatedOnline ? 'text-[#B69A75] font-semibold' : 'text-[#879487] font-semibold'}>
                    {isSimulatedOnline ? 'Standard TLS' : 'Enforced: Zero Sync'}
                  </span>
                </div>
              </div>

            </div>
          </div>

        </div>

      </div>
    </section>
  );
};
