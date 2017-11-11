package com.pricehero.serde

trait ReadWritePipe[In, T, Out] extends De[In, T] with Ser[T, Out]

