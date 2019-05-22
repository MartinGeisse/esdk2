.option norvc
    lui sp, 0x00001
    call main
hang:
    j hang
