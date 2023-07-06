(ns views.core
  (:require [clojure.string :as string]))

(enable-console-print!)

(defn remove-class [element class-name]
  (let [elem (js/document.querySelector element)]
    (.replace (.-className elem) (re-pattern (str "\\b" class-name "\\b")) "")))

(defn classes-of
  "Get the classes of an element as a Clojure keyword vector."
  [e]
  (let [words (-> e (.getAttribute "class") (string/split " "))]
    (mapv keyword words)))

(defn classes->str
  "Change a Clojure keyword seq into an HTML class string."
  [classes]
  (->> classes (mapv name) (string/join " ")))

(defn class-reset!
  "Unconditionally set the classes of an element."
  [e classes]
  (.setAttribute e "class" (classes->str classes))
  e)

(defn class-swap!
  "Update the classes of an element using a fn."
  [e f]
  (class-reset! e (f (classes-of e))))

(defn add-class!
  "Add a class to an element."
  [e class]
  (class-swap! e #(distinct (conj % (keyword class)))))

(defn remove-class!
  "Remove a class from an element."
  [e class]
  (class-swap! e (fn [current] (remove #(= % (keyword class)) current))))

(defn toggle-class!
  "Toggle between 2 classes, one of which is already on the element."
  [e class1 class2]
  (let [toggle-map {(keyword class1) (keyword class2), (keyword class2) (keyword class1)}]
    (class-swap! e #(replace toggle-map %))))

(defn guest-feature [event]
  (let [guest-form (js/document.querySelector ".guest-form")
        create-links-form (js/document.querySelector ".create-links")]
    (set! (.-className guest-form) (str (.-className guest-form) " hidden"))
    (remove-class! create-links-form "hidden"))
  (.preventDefault event))

(defonce counter (atom 0))

(defn add-new-link [_]
  (let [create-links-form (js/document.querySelector ".create-links")
        div (js/document.createElement "div")
        input (js/document.createElement "input")
        icon (js/document.createElement "i")
        removeIcon (js/document.createElement "i")]
    (.classList.add div "border" "border-slate-300" "rounded-md" "relative")
    (.classList.add input "px-2" "py-2" "w-full" "border-0" "focus:outline-0" "pl-8")
    (.classList.add icon "fas" "fa-link" "text-blue-500" "absolute" "top-5" "left-2" "transform" "-translate-y-1/2")
    (.classList.add removeIcon "fas" "fa-times" "text-red-500" "absolute" "top-5" "right-2" "transform" "-translate-y-1/2" "cursor-pointer")
    (.setAttribute input "type" "text")
    (.setAttribute input "name" "text")
    (.setAttribute input "placeholder" "Enter link...")
    (.appendChild div input)
    (.appendChild div icon)
    (.appendChild div removeIcon)
    (.appendChild create-links-form div)
    (swap! counter inc)
    (let [counter-element (js/document.querySelector ".counter")]
      (set! (.-textContent counter-element) @counter)
      (.addEventListener removeIcon "click" (fn [event]
                                              (.removeChild div)
                                              (swap! counter dec)
                                              (set! (.-textContent counter-element) @counter))))))

(defn mount-root []
  (let [button (js/document.querySelector ".guest-form")
        add-new-link-button (js/document.querySelector ".add-new-link")]
    (.addEventListener button "click" guest-feature)
    (.addEventListener add-new-link-button "click" add-new-link)))

(println "Click event example")

(mount-root)

