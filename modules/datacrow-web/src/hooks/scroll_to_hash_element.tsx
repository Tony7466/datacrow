import { useLayoutEffect, useState, useEffect, useRef } from "react";

interface ScrollToHashElementProps {
    /** On first run do we jump or scroll? */
    initialBehavior?: ScrollBehavior;
    behavior?: ScrollBehavior;
    inline?: ScrollLogicalPosition;
    block?: ScrollLogicalPosition;
}

const ScrollToHashElement = ({
    behavior = "auto",
    initialBehavior = "auto",
    inline = "nearest",
    block = "start",
}: ScrollToHashElementProps): null => {
    const [hash, setHash] = useState(window.location.hash);
    const [count, setCount] = useState(0);
    const originalListeners = useRef<{ [key: string]: Function }>({})

    // We need to know if this is the first run. If it is, we can do an instant jump, no scrolling.
    const [firstRun, setFirstRun] = useState(true);
    useEffect(() => setFirstRun(false), [])


    useEffect(() => {

        const handleLocationChange = () => {
            setHash(window.location.hash);

            // We increment count just so the layout effect will run if the hash is the same.
            // Otherwise the user might click a hashlink a second time and it won't go anywhere.
            setCount((count: number) => count + 1)
        };

        const onPopState = () => {
            window.dispatchEvent(new Event('locationchange'));
        }

        const addWindowListeners = () => {
            originalListeners.current.pushState = window.history.pushState;
            originalListeners.current.replaceState = window.history.replaceState;

            window.history.pushState = function(...args: any) {
                const result = originalListeners.current.pushState.apply(this, args);
                window.dispatchEvent(new Event('pushstate'));
                window.dispatchEvent(new Event('locationchange'));
                return result;
            };

            window.history.replaceState = function(...args: any) {
                const result = originalListeners.current.replaceState.apply(this, args);
                window.dispatchEvent(new Event('replacestate'));
                window.dispatchEvent(new Event('locationchange'));
                return result;
            };

            window.addEventListener('popstate', onPopState);
            window.addEventListener('locationchange', handleLocationChange);
        }

        // Cleanup the event listeners on component unmount
        const removeWindowListeners = () => {
            window.history.pushState = originalListeners.current.pushState as typeof window.history.pushState
            window.history.replaceState = originalListeners.current.replaceState as typeof window.history.replaceState
            window.removeEventListener('popstate', onPopState);
            window.removeEventListener('locationchange', handleLocationChange);
        }

        addWindowListeners()
        return removeWindowListeners;
    }, []);


    useLayoutEffect(() => {
        // check if there's a hash
        if (hash && hash.length > 0) {
            
            // function for scrolling
            const scrollToElement = (hash: string, retries = 10) => {
                const element = document.querySelector(hash);
                
                if (element) {
                    // wait a bit so that other scroll actions are completed first
                    setTimeout(function() {
                        element.scrollIntoView({
                            behavior: firstRun ? initialBehavior : behavior,
                            inline: inline,
                            block: block,
                        });
                    }, 250);
                } else if (retries > 0) {
                    // we're retrying so that it is sure that everything has been rendered
                    setTimeout(() => scrollToElement(hash, retries - 1), 100);
                }
            }
            scrollToElement(location.hash);
        }
    }, [hash, count, firstRun]);

    return null;
};

export default ScrollToHashElement;