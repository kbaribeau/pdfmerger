(ns pdfmerger.core
  (:import
    [com.lowagie.text Document]
    [com.lowagie.text.pdf PdfCopy PdfReader]
    [java.io FileOutputStream])
  (:gen-class))

(def directory (clojure.java.io/file "resources/"))
(def files (filter #(re-matches #".*pdf$" (.getName %)) (file-seq directory)))

(defn group [file]
  (clojure.string/replace (.getName file) #"p\d.pdf" ""))

(defn -main [& args]
  (let [groups (group-by group files)]
    (doseq [[nf old-files] groups]
      (let [new-filename (clojure.string/trim nf)
            document (new Document)
            copy (new PdfCopy document
                      (new FileOutputStream
                           (str (clojure.string/trim new-filename) ".pdf")))
            old-readers (map #(new PdfReader (clojure.java.io/input-stream %)) old-files)]
        (.open document)
        (doseq [r old-readers]
          (doseq [page-num (range 0 (.getNumberOfPages r))]
            (.addPage copy (.getImportedPage copy r (+ 1 page-num))))
          (.freeReader copy r)
        )
        (.close document)))))

