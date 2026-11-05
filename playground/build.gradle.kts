import com.github.gradle.node.npm.task.NpmTask

plugins { id("cql.node-gradle-conventions") }

tasks.named("npmInstall") { dependsOn(":engine:build") }

val npmRunBuild by
    tasks.registering(NpmTask::class) {
        dependsOn("npmInstall")
        args.set(listOf("run", "build"))
    }

val npmRunDev by
    tasks.registering(NpmTask::class) {
        dependsOn("npmInstall")
        args.set(listOf("run", "dev"))
        outputs.upToDateWhen { false }
        doNotTrackState("`npm run dev` starts a local dev server")
    }

tasks.named("build") { dependsOn(npmRunBuild) }
