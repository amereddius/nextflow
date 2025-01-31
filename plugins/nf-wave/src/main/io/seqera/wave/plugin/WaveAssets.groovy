/*
 * Copyright 2020-2022, Seqera Labs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.seqera.wave.plugin

import java.nio.file.Path

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import groovy.transform.Memoized
import nextflow.script.bundle.ModuleBundle
import nextflow.util.CacheHelper
/**
 * Hold assets required to fulfill wave container image build
 * 
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */
@Canonical
@CompileStatic
class WaveAssets {
    final String containerImage
    final ModuleBundle bundle
    final ContainerConfig containerConfig
    final String dockerFileContent
    final Path condaFile

    static fromImage(String containerImage) {
        new WaveAssets(containerImage)
    }

    static fromDockerfile(String dockerfile) {
        new WaveAssets(null, null, null, dockerfile)
    }

    String dockerFileEncoded() {
        return dockerFileContent
                ? dockerFileContent.bytes.encodeBase64()
                : null
    }

    String condaFileEncoded() {
        return condaFile
                ? condaFile.text.bytes.encodeBase64()
                : null
    }

    @Memoized
    String hashKey() {
        final allMeta = new ArrayList(10)
        allMeta.add( this.containerImage )
        allMeta.add( this.bundle?.fingerprint() )
        allMeta.add( this.containerConfig?.hashCode() )
        allMeta.add( this.dockerFileContent )
        allMeta.add( this.condaFile )
        return CacheHelper.hasher(allMeta).hash().toString()
    }
}
