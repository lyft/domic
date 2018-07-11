package com.lyft.domic.samples.performance

import android.app.Application
import android.os.Process
import com.lyft.domic.android.rendering.AndroidRenderer
import com.lyft.domic.api.rendering.Renderer
import io.reactivex.plugins.RxJavaPlugins
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

class PerformanceApplication : Application() {

    companion object {
        val renderer: Renderer = AndroidRenderer()
    }

    override fun onCreate() {
        super.onCreate()

        val computationScheduler = RxJavaPlugins.createComputationScheduler(RxThreadFactory(Process.THREAD_PRIORITY_BACKGROUND))
        RxJavaPlugins.setComputationSchedulerHandler { computationScheduler }
    }

    class RxThreadFactory(private val priority: Int) : ThreadFactory {

        private val idGenerator = AtomicInteger()

        override fun newThread(runnable: Runnable) = Thread(Runnable {
            Process.setThreadPriority(priority)
            runnable.run()
        }).apply {
            isDaemon = true
            name = "${idGenerator.incrementAndGet()}"
        }

    }
}
