import React, { useState } from 'react';
import { Copy, Check, Terminal, FileCode2 } from 'lucide-react';

interface CodeSnippet {
  id: string;
  filename: string;
  path: string;
  description: string;
  code: string;
}

const SNIPPETS: CodeSnippet[] = [
  {
    id: 'router',
    filename: 'ChatRepository.kt',
    path: 'com.example.hybridmind.data.ChatRepository',
    description: 'Dynamic hybrid routing: tries Gemini cloud first, catches network drops, and auto-falls back to LiteRT-LM without failing.',
    code: `// Dynamic Hybrid Router with Zero-Downtime Fallback
suspend fun generateResponse(sessionId: String, userMessage: String, imageData: ByteArray? = null): String {
    val isOnline = networkMonitor.isOnline.first()
    val session = chatDao.getAllSessions(currentUserId).find { it.id == sessionId }
        ?: return "Error: Session not found"
    
    val isOfflineSession = session.is_offline_only

    return withContext(Dispatchers.IO) {
        if (isOnline && !isOfflineSession) {
            try {
                // 1. Try Gemini 2.5 Flash cloud generation
                generateWithGemini(sessionId, userMessage, imageData)
            } catch (e: Exception) {
                Log.e("ChatRepository", "Gemini failed: \${e.message}. Attempting fallback...")
                
                // 2. Automated Fallback to On-Device LiteRT-LM
                if (isOfflineModelReady()) {
                    val offlineResponse = generateWithMediaPipe(sessionId, userMessage, imageData)
                    // Mark session as offline-only to guarantee privacy
                    chatDao.updateSession(session.copy(is_offline_only = true))
                    offlineResponse
                } else {
                    throw Exception("Online generation failed and offline model not downloaded.")
                }
            }
        } else {
            // 3. Pure On-Device Execution (Offline / Private Session)
            generateWithMediaPipe(sessionId, userMessage, imageData)
        }
    }
}`
  },
  {
    id: 'network',
    filename: 'NetworkMonitor.kt',
    path: 'com.example.hybridmind.core.NetworkMonitor',
    description: 'Reactive network stream using Android ConnectivityManager and Kotlin Coroutines Flow.',
    code: `// Reactive Android Connectivity Stream
class NetworkMonitor(private val context: Context) {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val isOnline: Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }
            override fun onLost(network: Network) {
                trySend(false)
            }
            override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
                val hasInternet = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                trySend(hasInternet)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)
        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()`
  },
  {
    id: 'pruner',
    filename: 'AutoPruneWorker.kt',
    path: 'com.example.hybridmind.workers.AutoPruneWorker',
    description: 'Background WorkManager garbage collector that safely purges offline chats older than 90 days.',
    code: `// WorkManager Background Garbage Collector
class AutoPruneWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val database = AppDatabase.getInstance(applicationContext)
            val ninetyDaysAgo = System.currentTimeMillis() - (90L * 24 * 60 * 60 * 1000)
            
            // Delete expired offline messages to conserve local flash memory
            val deletedCount = database.chatDao().deleteOfflineMessagesOlderThan(ninetyDaysAgo)
            Log.d("AutoPruneWorker", "Purged \$deletedCount expired offline messages.")
            
            Result.success()
        } catch (e: Exception) {
            Log.e("AutoPruneWorker", "Housekeeping prune failed", e)
            Result.retry()
        }
    }
}`
  },
  {
    id: 'litert',
    filename: 'LiteRTInitialization.kt',
    path: 'com.example.hybridmind.data.ChatRepository:50',
    description: 'Initializes LiteRT-LM Engine with OpenCL GPU delegate for high-throughput quantized Gemma 3n inference.',
    code: `// LiteRT-LM GPU Accelerated Setup for Gemma 3n
suspend fun initializeOfflineModel(modelPath: String) = withContext(Dispatchers.IO) {
    val file = File(modelPath)
    if (!file.exists() || file.length() < 100 * 1024 * 1024) {
        throw Exception("Invalid model file at \$modelPath")
    }

    // Configure Engine matching Google AI Edge benchmarks
    val engineConfig = EngineConfig(
        modelPath = modelPath,
        backend = Backend.GPU,       // OpenCL/Vulkan Mobile GPU
        visionBackend = Backend.GPU, // Vision MUST be GPU for Gemma 3n
        maxNumTokens = 2048,
        cacheDir = context.getExternalFilesDir(null)?.absolutePath
    )

    engine = Engine(engineConfig).also { it.initialize() }
    conversation = engine?.createConversation(
        ConversationConfig(
            samplerConfig = SamplerConfig(topK = 40, topP = 0.95f, temperature = 0.8f)
        )
    )
}`
  }
];

export const CodeDeepDive: React.FC = () => {
  const [activeTab, setActiveTab] = useState<string>('router');
  const [copied, setCopied] = useState<boolean>(false);

  const currentSnippet = SNIPPETS.find(s => s.id === activeTab) || SNIPPETS[0];

  const handleCopy = () => {
    navigator.clipboard.writeText(currentSnippet.code);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <section id="code" className="py-24 relative overflow-hidden bg-[#EAE7DF] border-t border-[#DED9CE]">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        {/* Header */}
        <div className="text-center max-w-3xl mx-auto mb-16">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-[#F7F5EF] border border-[#DED9CE] text-[#424A43] text-xs font-mono mb-4 shadow-subtle">
            <Terminal className="w-3.5 h-3.5 text-[#879487]" />
            <span>Native Kotlin Codebase</span>
          </div>
          <h2 className="text-3xl sm:text-5xl font-serif font-normal text-[#171B19] tracking-tight">
            Production-Grade Android Architecture
          </h2>
          <p className="mt-4 text-[#3A403C] text-base sm:text-lg leading-relaxed">
            Inspect the exact Kotlin implementations governing reactive connectivity, LiteRT-LM GPU delegates, and background pruning.
          </p>
        </div>

        {/* Tab Selection */}
        <div className="flex flex-wrap items-center justify-center gap-2.5 mb-6">
          {SNIPPETS.map(snippet => (
            <button
              key={snippet.id}
              onClick={() => setActiveTab(snippet.id)}
              className={`px-4 py-2.5 rounded-md text-xs sm:text-sm font-mono font-medium transition-all flex items-center gap-2 border ${
                activeTab === snippet.id
                  ? 'bg-[#171B19] border-[#171B19] text-[#F7F5EF] shadow-sm'
                  : 'bg-[#F7F5EF] border-[#DED9CE] text-[#575E59] hover:text-[#171B19] hover:bg-[#F1EFE8]'
              }`}
            >
              <FileCode2 className="w-4 h-4 text-[#B69A75]" />
              <span>{snippet.filename}</span>
            </button>
          ))}
        </div>

        {/* Code Editor Window (Graphite Dark Surface) */}
        <div className="bg-[#171B19] rounded-xl overflow-hidden border border-[#B69A75]/30 shadow-darkCard">
          
          {/* Window Header */}
          <div className="flex items-center justify-between px-6 py-4 bg-[#1E2320] border-b border-[#2C312E]">
            <div className="flex items-center gap-3">
              <div className="flex gap-1.5">
                <span className="w-2.5 h-2.5 rounded-full bg-[#DED9CE]/30"></span>
                <span className="w-2.5 h-2.5 rounded-full bg-[#DED9CE]/30"></span>
                <span className="w-2.5 h-2.5 rounded-full bg-[#DED9CE]/30"></span>
              </div>
              <span className="text-xs font-mono text-[#DCD7CD] hidden sm:inline">
                {currentSnippet.path}
              </span>
            </div>

            <button
              onClick={handleCopy}
              className="flex items-center gap-1.5 px-3 py-1.5 rounded bg-[#252B28] hover:bg-[#2E3531] border border-[#3A423D] text-xs font-mono text-[#EAE7DF] transition-all"
            >
              {copied ? <Check className="w-3.5 h-3.5 text-[#879487]" /> : <Copy className="w-3.5 h-3.5 text-[#B69A75]" />}
              <span>{copied ? 'Copied!' : 'Copy Code'}</span>
            </button>
          </div>

          {/* Description sub-banner */}
          <div className="px-6 py-3 bg-[#242926] border-b border-[#2C312E] text-xs text-[#DCD7CD] font-sans">
            <strong className="font-semibold text-[#F7F5EF]">Insight: </strong>
            {currentSnippet.description}
          </div>

          {/* Code Body */}
          <div className="p-6 bg-[#171B19] overflow-x-auto text-xs sm:text-[13px] font-mono text-[#EAE7DF] leading-relaxed">
            <pre>
              <code>{currentSnippet.code}</code>
            </pre>
          </div>

        </div>

      </div>
    </section>
  );
};
