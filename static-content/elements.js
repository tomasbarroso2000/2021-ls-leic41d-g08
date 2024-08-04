/**
 * Creates a html element
 */
function createElement(elementType, attributes, ...children) {
    const newElement = document.createElement(elementType);
    Object.keys(attributes).forEach((attribute) => {
        newElement[attribute] = attributes[attribute]
    })
    children.forEach(child => {
        if (typeof child === 'string') {
            newElement.appendChild(document.createTextNode(child));
        } else {
            newElement.appendChild(child);
        }
    });
    return newElement;
}

export function div(attributes, ...children) {
    return createElement("div", attributes, ...children);
}

export function ul(attributes, ...children) {
    return createElement("ul", attributes, ...children);
}

export function li(attributes, ...children) {
    return createElement("li", attributes, ...children);
}

export function a(attributes, ...children) {
    return createElement("a", attributes, ...children);
}

export function h1(attributes, ...children) {
    return createElement("h1", attributes, ...children);
}

export function p(attributes, ...children) {
    return createElement("p", attributes, ...children);
}

export function img(attributes, ...children) {
    return createElement("img", attributes, ...children);
}

export function input(attributes) {
    return createElement("input", attributes);
}

export function label(attributes, ...children) {
    return createElement("label", attributes, ...children);
}

export function button(attributes, ...children) {
    return createElement("button", attributes, ...children);
}

export function select(attributes, ...children) {
    return createElement("select", attributes, ...children);
}

export function option(attributes, ...children) {
    return createElement("option", attributes, ...children);
}