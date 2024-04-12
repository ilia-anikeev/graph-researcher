import React from "react";
import "./GraphResearchInfo.css";
import "../index.css"

export default class GraphMetadata extends React.Component{
    state={
        isOpen:false
    }
    render() {
        return( 
            <React.Fragment>
                <button className="button" onClick={() => {setTimeout(() => this.setState({isOpen:true}), 1000)}}>      Research      </button>
                {this.state.isOpen && <div className="GraphMetadata">
                    <div className="GraphMetadata-body" >
                        <h1 style={{textAlign: "center"}}>Info</h1>
                        <p>Bridges                                                                     no </p>  
                        <p>Articulation Points                                                 3</p>
                        <p>Connected Components                              (1, 2, 3, 4, 5)</p>
                    <button style={{alignSelf: "right"}} onClick={() => this.setState({isOpen:false})}>Close</button>
                    </div>
                </div>}
            </React.Fragment>
        )
    }
}
