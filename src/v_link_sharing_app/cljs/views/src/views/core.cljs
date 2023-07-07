(ns views.core
  (:require [clojure.string :as string]
            [reagent.impl.input :as input]
            [clojure.browser.dom :as dom]))

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
(defn show-save-button []
  (let [save-button (js/document.querySelector ".save-links")]
    (.classList.remove save-button "hidden")))

(defn guest-feature [event]
  (let [guest-form (js/document.querySelector ".guest-form")
        create-links-form (js/document.querySelector ".create-links")]
    (set! (.-className guest-form) (str (.-className guest-form) " hidden"))
    (remove-class! create-links-form "hidden")
    (show-save-button))
  (.preventDefault event))

(defonce counter (atom 0))

(defn remove-parent-node [node]
  (-> node .-parentElement (.remove)))

(defn remove-node [node]
  (.. node -parentNode (removeChild node)))

(defn remove-html-node [class]
  (let [node (.. js/document querySelector class)]
    (when node
      (remove-node node))))


(defn add-new-link [_]
  (let [create-links-form (js/document.querySelector ".links")
        div (js/document.createElement "div")
        input (js/document.createElement "input")
        linkInput (js/document.createElement "input")
        icon (js/document.createElement "i")
        removeText (js/document.createElement "p")]
    (.classList.add div "card" "bg-gray-700" "rounded-md" "relative" "pb-8" "mb-5" "flex" "flex-col")
    (.classList.add linkInput "input-field" "px-2" "py-2" "w-full" "border-0" "focus:outline-0" "pl-8" "mb-3" "flex-1")
    (.classList.add input "input-field" "px-2" "py-2" "w-full" "border-0" "focus:outline-0" "pl-8" "flex-1")
    (.classList.add icon "fa" "fa-link" "text-blue-500" "absolute" "top-12" "left-2" "transform" "-translate-y-1/2")
    (.classList.add removeText "remove-text" "text-xs" "cursor-pointer" "text-white" "text-end" "px-2" "py-2" "hover:text-red-500")
    (.setAttribute input "type" "text")
    (.setAttribute input "name" "text")
    (.setAttribute input "placeholder" "Enter platform...")
    (.setAttribute linkInput "type" "text")
    (.setAttribute linkInput "name" "link")
    (.setAttribute linkInput "placeholder" "Enter link...")
    (set! (.-innerHTML removeText) "Remove link")
    (.appendChild div removeText)
    (.appendChild div linkInput)
    (.appendChild div input)
    (.appendChild div icon)
    (.appendChild create-links-form div)
    (swap! counter inc)
    (let [counter-element (js/document.querySelector ".counter")]
      (set! (.-textContent counter-element) @counter))))


(enable-console-print!)

(defn remove-link [node]
  (.remove (.-parentNode node))
  (swap! counter dec)
  (let [counter-element (js/document.querySelector ".counter")]
    (set! (.-textContent counter-element) @counter)))
(def tablist (js/document.querySelector "[role=\"tablist\"]"))
(def tabButtons (.-children tablist))
(def tabPanels (-> tablist (.-parentElement) (.querySelectorAll "[role=\"tabpanel\"]")))

(defn tab-click-handler [e]
  (doseq [panel tabPanels]
    (set! (.-hidden panel) true))
  (doseq [button tabButtons]
    (.setAttribute button "aria-selected" "false"))
  (.setAttribute (.-currentTarget e) "aria-selected" "true")
  (.classList.add (.-currentTarget e) "border-b-indigo-500", "p-5", "border-b-8", "transition" "ease-in-out" "delay-150")
  (doseq [button tabButtons]
    (when (not= button (.-currentTarget e))
      (.classList.remove button "border-b-indigo-500", "p-5", "border-b-8", "duration-300", "transition")))
  (let [id (.-id (.-currentTarget e))
        currentTabPanel (some #(when (= (.getAttribute % "aria-labelledby") id) %) tabPanels)]
    (when currentTabPanel
      (set! (.-hidden currentTabPanel) false))))

(doseq [button tabButtons]
  (.addEventListener button "click" tab-click-handler))

(defn mount-root []
  (let [button (js/document.querySelector ".guest-form")
        add-new-link-button (js/document.querySelector ".add-new-link")]
    (.addEventListener button "click" guest-feature)
    (.addEventListener add-new-link-button "click" #(add-new-link %))
    (js/document.addEventListener "click"
                                  #(let [target (.-target %)]
                                     (let [closestRemoveText (.closest target ".remove-text")]
                                       (when closestRemoveText
                                         (remove-link target)
                                         (show-save-button)))))))









(println "Click event example")

(mount-root)