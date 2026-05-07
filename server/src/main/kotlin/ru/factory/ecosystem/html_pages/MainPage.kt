package ru.factory.ecosystem.html_pages

val MAIN_PAGE_HTML = """
    <!DOCTYPE html>
    <html lang="ru">
    <head>
        <meta charset="UTF-8">
        <title>Factory Control Panel</title>
        <script src="https://cdn.tailwindcss.com"></script>
    </head>
    <body class="bg-gray-900 text-gray-100 min-h-screen p-10 font-sans">
        <div class="max-w-4xl mx-auto">
            <h1 class="text-4xl font-black mb-10 text-blue-500 tracking-tight">FACTORY <span class="text-white">ECOSYSTEM</span></h1>
            
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-10">
                <div class="bg-gray-800 p-6 rounded-2xl border border-gray-700 shadow-xl">
                    <h2 class="text-xl font-bold mb-4 flex items-center gap-2">📸 Управление камерой</h2>
                    <div class="flex flex-col gap-3">
                        <button onclick="apiCall('/start-scan')" class="bg-green-600 hover:bg-green-500 py-3 rounded-xl font-bold transition-colors">▶ Запустить сканирование</button>
                        <button onclick="apiCall('/stop-scan')" class="bg-gray-700 hover:bg-gray-600 py-3 rounded-xl font-bold transition-colors">■ Остановить</button>
                    </div>
                </div>
                
                <div class="bg-gray-800 p-6 rounded-2xl border border-gray-700 shadow-xl">
                    <h2 class="text-xl font-bold mb-4 flex items-center gap-2">🧠 Тестирование ML</h2>
                    <p class="text-gray-400 text-sm mb-4">Проверка распознавания на статичном файле hand.png</p>
                    <button onclick="runTestML()" class="w-full bg-blue-600 hover:bg-blue-500 py-3 rounded-xl font-bold transition-colors shadow-lg shadow-blue-900/20">🔍 Выполнить тест ML</button>
                </div>
            </div>

            <!-- Результаты ML -->
            <div id="ml-result-wrapper" class="hidden animate-in fade-in duration-500">
                <details class="bg-gray-800 rounded-2xl border border-gray-700 shadow-2xl overflow-hidden" open>
                    <summary class="p-4 cursor-pointer font-bold bg-gray-750 hover:bg-gray-700 transition-colors select-none">📦 Результат ML анализа (JSON)</summary>
                    <div class="p-6 bg-black">
                        <pre id="ml-json-content" class="text-green-400 text-sm font-mono overflow-auto max-h-[500px]"></pre>
                    </div>
                </details>
            </div>
            
            <!-- Тост для уведомлений -->
            <div id="toast" class="fixed bottom-10 right-10 bg-blue-600 text-white px-6 py-3 rounded-full shadow-2xl transform translate-y-20 opacity-0 transition-all duration-300">
            </div>
        </div>

        <script>
            function showToast(msg) {
                const toast = document.getElementById('toast');
                toast.innerText = msg;
                toast.classList.remove('translate-y-20', 'opacity-0');
                setTimeout(() => {
                    toast.classList.add('translate-y-20', 'opacity-0');
                }, 3000);
            }

            async function apiCall(url) {
                try {
                    const response = await fetch(url);
                    const text = await response.text();
                    showToast(text);
                } catch (e) {
                    showToast('Ошибка: ' + e);
                }
            }

            async function runTestML() {
                const wrapper = document.getElementById('ml-result-wrapper');
                const pre = document.getElementById('ml-json-content');
                try {
                    const response = await fetch('/test-ml');
                    if (!response.ok) throw new Error(await response.text());
                    const data = await response.json();
                    pre.textContent = JSON.stringify(data, null, 4);
                    wrapper.classList.remove('hidden');
                    showToast('ML Тест завершен');
                } catch (e) {
                    showToast('Ошибка ML: ' + e.message);
                }
            }
        </script>
    </body>
    </html>
""".trimIndent()
