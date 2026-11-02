import SlickSlider from "react-slick";

// react-slick is CommonJS, so Vite 8 gives us its exports object as the default import
const Slider = SlickSlider.default ?? SlickSlider;

export default Slider;
