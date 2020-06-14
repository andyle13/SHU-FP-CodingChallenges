(defproject fp-assignment-1 "0.1.0-SNAPSHOT"
  :main fp-assignment-1.core
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [cheshire "5.9.0"]
                 [net.mikera/core.matrix "0.62.0"]]
  :repl-options {:init-ns fp-assignment-1.core}
  :jvm-opts ["-XX:+UseParallelGC" "-Xms1g" "-Xmx1g" "-XX:NewRatio=3"])