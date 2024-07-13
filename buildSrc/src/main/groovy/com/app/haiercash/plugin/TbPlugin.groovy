package com.app.haiercash.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class TbPlugin  implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def android = project.extensions.getByType(AppExtension)
        AutoTransform transform = new AutoTransform()
        android.registerTransform(transform)
    }



}