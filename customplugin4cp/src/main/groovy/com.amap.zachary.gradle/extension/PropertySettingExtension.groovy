package com.amap.zachary.gradle.extension

import org.gradle.api.tasks.StopExecutionException
class PropertySettingExtension{
    final  String CONFIGURATION_SEPARATOR = ":"
    def debug = false
    def settingDependsonTask
    def resettingDependsonTask
    def customsetting = []
    def replaceorigin = []
    def setting(String setstringorigin){
        def setstring = setstringorigin.tokenize(CONFIGURATION_SEPARATOR)
        if (setstring.size() != 3) {
            throw new StopExecutionException('please specify class:filed:value')
        } else  {
            customsetting << setstringorigin
        }
    }
    def setting(Map m){
        def setstring = m['class'] + CONFIGURATION_SEPARATOR + m['field'] + CONFIGURATION_SEPARATOR + m['value']
        customsetting << setstring
    }
    def replace(String replacestringorigin){
        def replacestring = replacestringorigin.tokenize(CONFIGURATION_SEPARATOR)
        if (replacestring.size() != 3) {
            throw new StopExecutionException('please specify class:reg:value')
        } else  {
            replaceorigin << replacestringorigin
        }
    }
    def replace(Map m){
        def replacestringorigin = m['class'] + CONFIGURATION_SEPARATOR + m['reg'] + CONFIGURATION_SEPARATOR + m['value']
        replaceorigin << replacestringorigin
    }
}