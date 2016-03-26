class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
		
        }
		//"/download/$root/$path**"(controller: "download")

        "/"(view:"/index")
        "500"(view:'/error')
	}
}
