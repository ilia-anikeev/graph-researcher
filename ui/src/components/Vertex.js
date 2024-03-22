import React from "react"
import Draggable from 'react-draggable'
import './Vertex.css'

function Vertex(){
    const noderef = React.useRef(null);
    return (
            <Draggable nodeRef={noderef}
                axis="both"
                defaultPosition={{x: 0, y: 0}}
                position={null}
                grid={[1,1]}
                scale={1}
            >
                <div className='vertex' ref={noderef}></div>
            </Draggable>
    )
}

export default Vertex