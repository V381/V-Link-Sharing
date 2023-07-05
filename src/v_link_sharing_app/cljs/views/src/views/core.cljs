(ns views.core)

(enable-console-print!)
(defn remove-class [element class-name]
  (let [elem (.-classList element)]
    (.remove elem class-name)))

(defn show-register-form [_]
  (let [login-form (.querySelector js/document ".login")
        register-form (.querySelector js/document ".register-form")
        register-text (.querySelector js/document ".register-text")]
    (aset login-form "className" (str (.-className login-form) " hidden")) 
    (aset register-text "className" (str (.-className register-text) " hidden"))
    (when register-form
      (remove-class register-form "hidden"))))

(defn jareMoje []
  (println "Hello World"))

(defn mount-root []
  (let [button (.querySelector js/document ".register")]
    (.addEventListener button "click" show-register-form)))

(println "Click event example")

(mount-root)

