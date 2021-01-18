/*
 * Copyright 2021 HM Revenue & Customs
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
 */

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import play.api.{ Configuration, Environment, Logger }
import uk.gov.hmrc.fsetlaunchpadgateway.config.{ WSHttp, WSHttpExternal, WSHttpNormal }

class Module(val environment: Environment, val configuration: Configuration) extends AbstractModule {
  override def configure(): Unit = {
    startUpMessage()

    bind(classOf[WSHttp])
      .annotatedWith(Names.named("httpNormal"))
      .to(classOf[WSHttpNormal]).asEagerSingleton()

    bind(classOf[WSHttp])
      .annotatedWith(Names.named("httpExternal"))
      .to(classOf[WSHttpExternal]).asEagerSingleton()
  }

  private def startUpMessage() = {
    val appName = configuration.getOptional[String]("appName").orElse(throw new RuntimeException(s"No configuration value found for 'appName'"))
    Logger.info(s"Starting ${appName.getOrElse("NOT-SET")} in mode ${environment.mode}")
    if (environment.mode == play.api.Mode.Prod) {
      Logger.info("In Prod White list filter should be enabled, please check that in following messages in this log")
    }
  }
}
